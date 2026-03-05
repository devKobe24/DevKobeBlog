package com.kobe.devkobeblog.post.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * packageName    : com.kobe.devkobeblog.post.domain
 * fileName       : Category
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 28.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 28.        kobe / Minsung Kang       최초 생성
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_slug", columnNames = "slug")
        }
)
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * ✅ 표시용 이름
     * - "Backend Development", "This is JAVA" 같은 원본 폴더명/카테고리명 그대로 유지 가능
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * ✅ URL/식별용 슬러그
     * - "backend-development", "this-is-java"
     * - 컨트롤러/쿼리스트링/URL path에서 사용
     */
    @Column(nullable = false, unique = true, length = 100)
    private String slug;

    // 양방향 매핑 (선택 사항이지만, 카테고리별 글 개수 샐 때 유용)
    @OneToMany(mappedBy = "category")
    private List<Post> posts = new ArrayList<>();

    public Category(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public void rename(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }
}
