package com.kobe.devkobeblog.post.domain;

import com.kobe.devkobeblog.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

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

    @Builder
    public Post(
            String title,
            String content,
            String thumbnail,
            String filePath,
            LocalDateTime publishedAt,
            PostStatus status
    ) {
        this.title = title;
        this.content = content;
        this.thumbnail = thumbnail;
        this.filePath = filePath;
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
