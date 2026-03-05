package com.kobe.devkobeblog.post.domain;

import com.kobe.devkobeblog.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * packageName    : com.kobe.devkobeblog.post.domain
 * fileName       : Post
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_post_category_slug",
                        columnNames = {"category_id", "slug"}
                )
        }
)
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "LONGTEXT")
    private String content;

    private String thumbnail;

    @Column(nullable = false, unique = true)
    private String filePath;

    // ✅ URL path segment #2 (e.g. "protocol")
    @Column(name = "slug", nullable = false, length = 200)
    private String slug;

    private LocalDateTime publishedAt;

    @Enumerated(EnumType.STRING)
    private PostStatus status; // PUBLIC, PRIVATE, DELETED

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "post_tag",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    public void setCategory(Category category) {
        this.category = category;
        category.getPosts().add(this);
    }

    public void updateTags(Set<Tag> newTags) {
        this.tags.clear();
        this.tags.addAll(newTags);
    }

    /**
     * ✅ 권장: filePath 기반으로 categorySlug/slug가 바뀔 일은 거의 없지만,
     * 폴더명/파일명 변경을 지원하려면 update에 포함시키는 편이 운영상 편함.
     */
    public void updateRouting(String slug) {
        this.slug = slug;
    }

    @Builder
    public Post(
            String title,
            String content,
            String thumbnail,
            String filePath,
            String slug,
            LocalDateTime publishedAt,
            PostStatus status
    ) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
        this.slug = slug;
        this.publishedAt = publishedAt;
        this.status = status;
    }

    public void update(
            String title,
            String content,
            String thumbnail,
            LocalDateTime publishedAt,
            PostStatus status
    ) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.publishedAt = publishedAt;
        this.status = status;
    }

    public void delete() {
        this.status = PostStatus.DELETED;
    }
}
