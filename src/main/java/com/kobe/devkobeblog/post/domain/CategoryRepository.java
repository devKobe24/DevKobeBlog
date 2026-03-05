package com.kobe.devkobeblog.post.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * packageName    : com.kobe.devkobeblog.post.domain
 * fileName       : CategoryRepository
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 28.        kobe / Minsung Kang       최초 생성
 */
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // ✅ URL/query에서 쓸 키 (예: "network", "backend-development")
    Optional<Category> findBySlug(String slug);

    // ✅ 화면 표시용 이름 (기존 호환)
    Optional<Category> findByName(String name);

    boolean existsBySlug(String slug);
}
