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
    Optional<Category> findByName(String name);
}
