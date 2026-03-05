package com.kobe.devkobeblog.post.service;

import com.kobe.devkobeblog.common.component.MarkdownProcessor;
import com.kobe.devkobeblog.common.component.SlugUtils;
import com.kobe.devkobeblog.post.domain.*;
import com.kobe.devkobeblog.post.dto.PostParseResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * packageName    : com.kobe.devkobeblog.post.service
 * fileName       : PostSyncWorker
 * author         : kobe / Minsung Kang
 * date           : 2026. 3. 6.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 3. 6.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class PostSyncWorker {

    private final MarkdownProcessor markdownProcessor;
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processSinglePostTx(Path filePath, Path gitRoot) throws IOException {
        processSinglePost(filePath, gitRoot);
    }

    private void processSinglePost(Path filePath, Path gitRoot) throws IOException {
        // 0) file_path (upsert key)
        String relativePath = SlugUtils.normalizePath(gitRoot.relativize(filePath).toString());

        // 1) category
        String categoryName = filePath.getParent().getFileName().toString();
        String categorySlug = SlugUtils.slugify(categoryName);
        Category category = getOrCreateCategory(categoryName, categorySlug);

        // 2) filename -> baseSlug
        String filename = filePath.getFileName().toString();
        String baseSlug = SlugUtils.extractSlugFromFilename(filename);

        // 3) parse
        PostParseResult result = markdownProcessor.process(filePath, gitRoot);
        Set<Tag> tags = getOrCreateTags(result.tags());
        PostStatus status = result.isPublic() ? PostStatus.PUBLIC : PostStatus.PRIVATE;

        // 4) upsert 대상 조회 (tags fetch)
        Post post = postRepository.findByFilePathWithTags(relativePath).orElse(null);
        Long excludeId = (post != null && post.getId() != null) ? post.getId() : -1L;

        // 5) 최종 slug 결정 (중복이면 suffix, 중복이 또 있으면 -1, -2 ...)
        String finalSlug = resolveFinalSlug(category, baseSlug, filename, relativePath, excludeId);

        if (post == null) {
            post = Post.builder()
                    .title(result.title())
                    .content(result.contentHtml())
                    .thumbnail(result.thumbnail())
                    .filePath(relativePath)
                    .slug(finalSlug)
                    .publishedAt(result.date())
                    .status(status)
                    .build();

            post.setCategory(category);
            post.updateTags(tags);
            postRepository.save(post);
        } else {
            post.update(result.title(), result.contentHtml(), result.thumbnail(), result.date(), status);
            post.updateRouting(finalSlug);

            if (post.getCategory() == null || !isSameCategory(post.getCategory(), category)) {
                post.setCategory(category);
            }

            post.updateTags(tags);
        }
    }

    private String resolveFinalSlug(Category category,
                                    String baseSlug,
                                    String filename,
                                    String relativePath,
                                    Long excludeId) {

        // category id가 없으면(비정상) 그냥 base로
        if (category.getId() == null) return baseSlug;

        String finalSlug = baseSlug;

        // suffix 후보: 파일명 날짜 prefix -> 없으면 file_path hash
        String suffix = SlugUtils.extractDatePrefix(filename);
        if (suffix.isBlank()) {
            suffix = Integer.toHexString(relativePath.hashCode());
        }

        // ✅ 중복이 있으면 while로 안전하게 회피
        int i = 0;
        while (postRepository.existsByCategory_IdAndSlugAndIdNot(category.getId(), finalSlug, excludeId)) {
            finalSlug = baseSlug + "-" + suffix + (i == 0 ? "" : "-" + i);
            i++;
        }

        return finalSlug;
    }

    private Category getOrCreateCategory(String name, String slug) {
        Optional<Category> bySlug = categoryRepository.findBySlug(slug);
        if (bySlug.isPresent()) {
            Category existing = bySlug.get();
            if (!existing.getName().equals(name)) {
                existing.rename(name, slug);
            }
            return existing;
        }

        Optional<Category> byName = categoryRepository.findByName(name);
        if (byName.isPresent()) {
            Category existing = byName.get();
            if (existing.getSlug() == null || existing.getSlug().isBlank() || !existing.getSlug().equals(slug)) {
                existing.rename(name, slug);
            }
            return existing;
        }

        try {
            return categoryRepository.save(new Category(name, slug));
        } catch (DataIntegrityViolationException e) {
            return categoryRepository.findBySlug(slug)
                    .orElseGet(() -> categoryRepository.findByName(name).orElseThrow(() -> e));
        }
    }

    private Set<Tag> getOrCreateTags(List<String> tagNames) {
        Set<Tag> tags = new HashSet<>();
        for (String name : tagNames) {
            if (name == null || name.isBlank()) continue;
            String trimmed = name.trim();
            Tag tag = tagRepository.findByName(trimmed)
                    .orElseGet(() -> tagRepository.save(new Tag(trimmed)));
            tags.add(tag);
        }
        return tags;
    }

    private static boolean isSameCategory(Category a, Category b) {
        if (a == null || b == null) return false;
        if (a.getId() != null && b.getId() != null) return a.getId().equals(b.getId());
        return a.getSlug() != null && a.getSlug().equals(b.getSlug());
    }
}
