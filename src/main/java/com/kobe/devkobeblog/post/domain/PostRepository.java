package com.kobe.devkobeblog.post.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * packageName    : com.kobe.devkobeblog.post.domain
 * fileName       : PostRepository
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */
public interface PostRepository extends JpaRepository<Post, Long> {

    // ✅ sync/upsert용 (원본 md 파일 기준)
    Optional<Post> findByFilePath(String filePath);

    // ✅ URL 라우팅용: /posts/{categorySlug}/{slug}
    // Post.category.slug를 따라가서 조회
    Optional<Post> findByCategory_SlugAndSlug(String categorySlug, String slug);

    // ✅ URL 라우팅(공개글만): 실제 서비스에서는 이걸 컨트롤러에서 쓰는 걸 권장
    Optional<Post> findByCategory_SlugAndSlugAndStatus(String categorySlug, String slug, PostStatus status);

    // 삭제되지 않은 모든 게시글 조회 (Soft Delete 로직용)
    List<Post> findAllByStatusNot(PostStatus status);

    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    // 공개 게시글 목록 (sitemap 등용)
    List<Post> findAllByStatusOrderByPublishedAtDesc(PostStatus status);

    // 카테고리별 조회
    Page<Post> findAllByCategoryAndStatus(Category category, PostStatus status, Pageable pageable);

    // ✅ (선택) 카테고리 slug로 바로 조회하고 싶으면 이거 추가하면 편함
    Page<Post> findAllByCategory_SlugAndStatus(String categorySlug, PostStatus status, Pageable pageable);

    // 태그별 조회
    Page<Post> findAllByTagsNameAndStatus(String tagName, PostStatus status, Pageable pageable);

    // 제목 검색 (대소문자 무시, 공개 글만)
    Page<Post> findByTitleContainingIgnoreCaseAndStatus(String keyword, PostStatus status, Pageable pageable);
}
