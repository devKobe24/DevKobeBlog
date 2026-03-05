package com.kobe.devkobeblog.post.service;

import com.kobe.devkobeblog.common.component.GitUtils;
import com.kobe.devkobeblog.common.component.MarkdownProcessor;
import com.kobe.devkobeblog.common.component.SlugUtils;
import com.kobe.devkobeblog.post.domain.*;
import com.kobe.devkobeblog.post.dto.PostParseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * packageName    : com.kobe.devkobeblog.post.service
 * fileName       : PostSyncService
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSyncService {

    private final GitUtils gitUtils;
    private final MarkdownProcessor markdownProcessor;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final java.util.concurrent.locks.ReentrantLock syncLock = new java.util.concurrent.locks.ReentrantLock();

    @Async("syncTaskExecutor")
    public void syncPosts() {
        if (!syncLock.tryLock()) {
            log.warn("syncPosts is already running. Skip this run.");
            return;
        }

        boolean completed = false;
        Set<String> processedFilePaths = new HashSet<>();

        try {
            Path gitRoot = gitUtils.sync();
            Path postsDir = gitRoot.resolve("posts");

            if (Files.exists(postsDir)) {
                try (Stream<Path> paths = Files.walk(postsDir)) {
                    paths.filter(p -> p.toString().endsWith(".md"))
                            .forEach(filePath -> {
                                String rel = null;
                                try {
                                    rel = normalizedPath(gitRoot.relativize(filePath).toString());
                                    // ✅ 파일 1개 단위 트랜잭션(권장)
                                    processSinglePostTx(filePath, gitRoot);
                                    processedFilePaths.add(rel);
                                } catch (Exception e) {
                                    log.error("Failed to process file: {}", (rel != null ? rel : filePath), e);
                                    // 운영 안정성: 한 파일 실패로 전체 sync를 중단하지 않음
                                }
                            });
                }
            }

            completed = true;
            log.info("Post sync scan completed. processed={}", processedFilePaths.size());

        } catch (Exception e) {
            log.error("Failed to sync posts.", e);
        } finally {
            try {
                if (completed && !processedFilePaths.isEmpty()) {
                    // ✅ delete phase는 성공한 경우에만
                    handleDeletedPostsTx(processedFilePaths);
                    log.info("Post sync completed successfully.");
                } else {
                    log.warn("Post sync did NOT complete or processed is empty. Skipping delete phase.");
                }
            } finally {
                syncLock.unlock();
            }
        }
    }

    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    protected void processSinglePostTx(Path filePath, Path gitRoot) throws IOException {
        processSinglePost(filePath, gitRoot);
    }

    @Transactional
    protected void handleDeletedPostsTx(Set<String> processedFilePaths) {
        handleDeletedPosts(processedFilePaths);
    }

    private void processSinglePost(Path filePath, Path gitRoot) throws IOException {
        // 0. filePath(상대경로) - sync/upsert 고유키
        String relativePath = normalizedPath(gitRoot.relativize(filePath).toString());

        // 1. 카테고리 추출 (폴더명) + slugify
        // 예: posts/Backend Development/2026-03-03-Smoke-Test.md -> name="Backend Development", slug="backend-development"
        String categoryName = filePath.getParent().getFileName().toString();
        String categorySlug = SlugUtils.slugify(categoryName);

        // 2. Category upsert (slug 기준)
        Category category = getOrCreateCategory(categoryName, categorySlug);

        // 3. Post slug 추출 (파일명에서 날짜 제거 + slugify)
        // 예: 2026-03-05-protocol.md -> "protocol"
        String filename = filePath.getParent().getFileName().toString();
        String postSlug = SlugUtils.extractSlugFromFilename(filename);

        // 4. 파싱 및 태그 준비
        PostParseResult result = markdownProcessor.process(filePath, gitRoot);
        Set<Tag> tags = getOrCreateTags(result.tags());
        PostStatus status = result.isPublic() ? PostStatus.PUBLIC : PostStatus.PRIVATE;

        // 5. Post 엔티티 저장/업데이트 (filePath 기준 upsert)
        Post post = postRepository.findByFilePath(relativePath).orElse(null);

        if (post == null) {
            post = Post.builder()
                    .title(result.title())
                    .content(result.contentHtml())
                    .thumbnail(result.thumbnail())
                    .filePath(relativePath)
                    .slug(postSlug)
                    .publishedAt(result.date())
                    .status(status)
                    .build();

            // 연관관계/태그 세팅 후 저장(한 번에)
            post.setCategory(category);
            post.updateTags(tags);

            postRepository.save(post);
        } else {
            post.update(result.title(), result.contentHtml(), result.thumbnail(), result.date(), status);
            post.updateRouting(postSlug);

            // 카테고리 변경이 생길 수도 있으니(폴더 이동) 안전하게 반영
            if (post.getCategory() == null || !isSameCategory(post.getCategory(), category)) {
                post.setCategory(category);
            }

            post.updateTags(tags);
            // save 호출은 선택(영속 상태면 dirty checking으로 반영됨)
        }
    }

    private Category getOrCreateCategory(String name, String slug) {

        // 1. slug로 조회
        Optional<Category> bySlug = categoryRepository.findBySlug(slug);
        if (bySlug.isPresent()) {
            Category existing = bySlug.get();

            if (!existing.getName().equals(name)) {
                existing.rename(name, slug);
            }

            return existing;
        }

        // 2. name으로 조회 (레거시 데이터 대응)
        Optional<Category> byName = categoryRepository.findByName(name);
        if (byName.isPresent()) {
            Category existing = byName.get();

            if (existing.getSlug() == null ||
                    existing.getSlug().isBlank() ||
                    !existing.getSlug().equals(slug)) {

                existing.rename(name, slug);
            }

            return existing;
        }

        // 3. 신규 생성
        return categoryRepository.save(new Category(name, slug));
    }

    private Set<Tag> getOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            if (name == null || name.isBlank()) {
                continue;
            }
            String trimmed = name.trim();
            Tag tag = tagRepository.findByName(trimmed)
                    .orElseGet(() -> tagRepository.save(new Tag(trimmed)));
            tags.add(tag);
        }
        return tags;
    }

    private void handleDeletedPosts(Set<String> processedFilePaths) {
        // DB에는 있지만(DELETED 제외), 방금 처리된 목록에는 없는 글 조회
        List<Post> activePosts = postRepository.findAllByStatusNot(PostStatus.DELETED);

        for (Post post : activePosts) {
            String fp = normalizedPath(post.getFilePath());
            if (!processedFilePaths.contains(fp)) {
                log.info("Marking post as DELETED: {}", post.getTitle());
                post.delete(); // Soft Delete 수행
            }
        }
    }

    private static String normalizedPath(String path) {
        return path.replace("\\", "/");
    }

    private static boolean isSameCategory(Category a, Category b) {
        if (a == null || b == null) {
            return false;
        }
        // 둘 다 저장된 엔티티면 id로 비교
        if (a.getId() != null && b.getId() != null) {
            return a.getId().equals(b.getId());
        }
        // 저장 전/임시 상태 대비: slug로 비교
        return a.getSlug() != null && a.getSlug().equals(b.getSlug());
    }
}
