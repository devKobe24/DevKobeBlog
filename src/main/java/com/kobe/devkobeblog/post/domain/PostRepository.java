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
    Optional<Post> findByFilePath(String filePath);

    // 삭제되지 않은 모든 게시글 조회 (Soft Delete 로직용)
    List<Post> findAllByStatusNot(PostStatus status);

    Page<Post> findAllByStatus(PostStatus status, Pageable pageable);

    // 카테고리별 조회
    Page<Post> findAllByCategoryAndStatus(Category category, PostStatus status, Pageable pageable);

    // 태그별 조회
    Page<Post> findAllByTagsNameAndStatus(String tagName, PostStatus status, Pageable pageable);
}
