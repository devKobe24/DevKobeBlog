package com.kobe.devkobeblog.post.service;

import com.kobe.devkobeblog.common.component.GitUtils;
import com.kobe.devkobeblog.common.component.MarkdownProcessor;
import com.kobe.devkobeblog.post.domain.*;
import com.kobe.devkobeblog.post.dto.PostParseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
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

    @Async("syncTaskExecutor")
    @Transactional
    public void syncPosts() {
        try {
            // 1. Git 동기화 (Clone or Pull)
            Path gitRoot = gitUtils.sync();
            Path postsDir = gitRoot.resolve("posts"); // 글 저장 폴더

            // 2. 파일 처리 및 DB 반영 (Upsert)
            Set<String> processedFilePaths = new HashSet<>();

            if (Files.exists(postsDir)) {
                try (Stream<Path> paths = Files.walk(postsDir)) {
                    paths.filter(p -> p.toString().endsWith(".md"))
                            .forEach(filePath -> {
                                try {
                                    processSinglePost(filePath, gitRoot);
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                                // Git Root 기준 상대 경로 저장 (예: posts/java/test.md)
                                processedFilePaths.add(gitRoot.relativize(filePath).toString());
                            });
                }
            }

            // 3. 삭제된 파일 처리 (Soft Delete)
            handleDeletedPosts(processedFilePaths);

            log.info("Post sync completed successfully.");

        } catch (IOException | GitAPIException e) {
            log.error("Failed to sync posts.", e);
            throw new RuntimeException("Git Sync Failed", e);
        }
    }

    private void processSinglePost(Path filePath, Path gitRoot) throws IOException {
        // 1. 카테고리 추출 (폴더명)
        // 예: posts/Java/effective-java.md -> "Java"
        String categoryName = filePath.getParent().getFileName().toString();
        Category category = getOrCreateCategory(categoryName);

        // 2. 파싱 및 태그 준비
        PostParseResult result = markdownProcessor.process(filePath, gitRoot);
        Set<Tag> tags = getOrCreateTags(result.tags());

        // 3. Post 엔티티 저장/업데이트
        String relativePath = gitRoot.relativize(filePath).toString();
        Post post = postRepository.findByFilePath(relativePath).orElse(null);
        PostStatus status = result.isPublic() ? PostStatus.PUBLIC : PostStatus.PRIVATE;

        if (post == null) {
            post = Post.builder()
                    .title(result.title())
                    .content(result.contentHtml())
                    .thumbnail(result.thumbnail())
                    .filePath(relativePath)
                    .publishedAt(result.date())
                    .status(status)
                    .build();
            postRepository.save(post);
        } else {
            post.update(result.title(), result.contentHtml(), result.thumbnail(), result.date(), status);
        }

        // [핵심] 연관관계 설정
        post.setCategory(category);
        post.updateTags(tags);
    }

    private Category getOrCreateCategory(String name) {
        return categoryRepository.findByName(name)
                .orElseGet(() -> categoryRepository.save(new Category(name)));
    }

    private Set<Tag> getOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            Tag tag = tagRepository.findByName(name)
                    .orElseGet(() -> tagRepository.save(new Tag(name)));
            tags.add(tag);
        }
        return tags;
    }

    private void handleDeletedPosts(Set<String> processedFilePaths) {
        // DB에는 있지만(DELETED 제외), 방금 처리된 목록에는 없는 글 조회
        List<Post> activePosts = postRepository.findAllByStatusNot(PostStatus.DELETED);

        for (Post post : activePosts) {
            if (!processedFilePaths.contains(post.getFilePath())) {
                log.info("Marking post as DELETED: {}", post.getTitle());
                post.delete(); // Soft Delete 수행
            }
        }
    }
}
