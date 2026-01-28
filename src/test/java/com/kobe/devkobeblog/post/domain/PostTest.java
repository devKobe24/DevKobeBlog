package com.kobe.devkobeblog.post.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : com.kobe.devkobeblog.post.domain
 * fileName       : PostTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */
class PostTest {

    @Test
    @DisplayName("Post 객체가 Builder로 정상적으로 생성된다")
    void createPost_Should_Bind_Fields_Correctly() {
        // Given
        String title = "테스트 제목";
        String content = "테스트 내용";
        String thumbnail = "thumb.png";
        String filePath = "posts/test.md";
        LocalDateTime now = LocalDateTime.now();
        PostStatus status = PostStatus.PUBLIC;

        // When
        Post post = Post.builder()
                .title(title)
                .content(content)
                .thumbnail(thumbnail)
                .filePath(filePath)
                .publishedAt(now)
                .status(status)
                .build();

        // Then
        assertThat(post.getTitle()).isEqualTo(title);
        assertThat(post.getContent()).isEqualTo(content);
        assertThat(post.getThumbnail()).isEqualTo(thumbnail);
        assertThat(post.getFilePath()).isEqualTo(filePath);
        assertThat(post.getPublishedAt()).isEqualTo(now);
        assertThat(post.getStatus()).isEqualTo(status);
    }

    @Test
    @DisplayName("update 메서드 호출 시 필드 값이 변경된다")
    void update_Should_Change_Fields() {
        // Given
        Post post = Post.builder()
                .title("원래 제목")
                .content("원래 내용")
                .thumbnail("old.png")
                .filePath("posts/fixed.md") // 파일 경로는 식별자이므로 안 바뀜
                .status(PostStatus.PRIVATE)
                .publishedAt(LocalDateTime.of(2023, 1, 1, 0, 0))
                .build();

        String newTitle = "바뀐 제목";
        String newContent = "바뀐 내용";
        String newThumbnail = "new.png";
        LocalDateTime newDate = LocalDateTime.now();
        PostStatus newStatus = PostStatus.PUBLIC;

        // When
        post.update(newTitle, newContent, newThumbnail, newDate, newStatus);

        // Then
        assertThat(post.getTitle()).isEqualTo(newTitle);
        assertThat(post.getContent()).isEqualTo(newContent);
        assertThat(post.getThumbnail()).isEqualTo(newThumbnail);
        assertThat(post.getPublishedAt()).isEqualTo(newDate);
        assertThat(post.getStatus()).isEqualTo(newStatus);

        // 중요: 식별자인 filePath는 update 메서드에 의해 변경되지 않아야 함
        assertThat(post.getFilePath()).isEqualTo("posts/fixed.md");
    }

    @Test
    @DisplayName("delete 메서드 호출 시 status가 DELETED로 변경된다 (Soft Delete)")
    void delete_Should_Change_Status_To_DELETED() {
        // Given
        Post post = Post.builder()
                .title("삭제될 글")
                .status(PostStatus.PUBLIC)
                .build();

        // When
        post.delete();

        // Then
        assertThat(post.getStatus()).isEqualTo(PostStatus.DELETED);
    }

}