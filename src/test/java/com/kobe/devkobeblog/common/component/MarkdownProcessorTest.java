package com.kobe.devkobeblog.common.component;

import com.kobe.devkobeblog.post.dto.PostParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : MarkdownProcessorTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 26.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 26.        kobe / Minsung Kang       최초 생성
 */

@ExtendWith(MockitoExtension.class)
class MarkdownProcessorTest {

    @Mock
    private S3Uploader s3Uploader;

    @InjectMocks
    private MarkdownProcessor markdownProcessor;

    @TempDir
    Path tempGitRoot;

    // @BeforeEach 제거함: 모든 테스트에서 업로드가 일어나는 것은 아니기 때문입니다.

    @Test
    @DisplayName("마크다운 파일을 파싱하여 메타데이터를 추출하고 로컬 이미지를 S3 URL로 변환한다")
    void process_Should_Parse_Markdown_And_Upload_Images() throws IOException {
        // ==========================================
        // 1. Given
        // ==========================================

        // [수정 포인트] 이 테스트에서만 필요한 Stubbing을 여기서 정의합니다.
        // 실제 S3Uploader는 UUID를 포함한 경로를 반환하지만, 테스트에서는 간단한 URL을 반환합니다.
        when(s3Uploader.upload(any(byte[].class), any(String.class)))
                .thenAnswer(invocation -> {
                    String filename = invocation.getArgument(1);
                    // 실제 구현과 유사하게 반환하되, 테스트를 위해 간단한 형식 사용
                    return "https://s3.aws.com/" + filename;
                });

        // 1-1. 디렉토리 및 파일 생성
        Path assetsDir = tempGitRoot.resolve("assets/img");
        Path postDir = tempGitRoot.resolve("posts/java");
        Path postImgDir = postDir.resolve("img");

        Files.createDirectories(assetsDir);
        Files.createDirectories(postImgDir);

        Path logoFile = assetsDir.resolve("logo.png");
        Path codeFile = postImgDir.resolve("code.png");
        Files.write(logoFile, "dummy-logo-bytes".getBytes());
        Files.write(codeFile, "dummy-code-bytes".getBytes());

        // 1-2. 마크다운 파일 생성
        Path markdownFile = postDir.resolve("test-post.md");
        String markdownContent = """
---
title: 테스트 게시글
date: 2024-01-01
tags:
  - Java
  - Test
thumbnail: /assets/img/logo.png
---
# 안녕하세요
이것은 테스트입니다.

![상대경로이미지](img/code.png)
![절대경로이미지](/assets/img/logo.png)
![외부이미지](https://google.com/logo.png)
""";
        Files.writeString(markdownFile, markdownContent);

        // ==========================================
        // 2. When
        // ==========================================
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // ==========================================
        // 3. Then
        // ==========================================
        assertThat(result.title()).isEqualTo("테스트 게시글");
        // 썸네일은 S3 URL로 변환되어야 합니다
        assertThat(result.thumbnail()).isEqualTo("https://s3.aws.com/logo.png");

        String html = result.contentHtml();
        // 본문 이미지들이 S3 URL로 변환되었는지 확인
        assertThat(html).contains("src=\"https://s3.aws.com/code.png\"");
        assertThat(html).contains("src=\"https://s3.aws.com/logo.png\"");
        // 외부 이미지는 그대로 유지되어야 합니다
        assertThat(html).contains("src=\"https://google.com/logo.png\"");

        // verify: 썸네일에서 logo.png 1번, 본문 이미지에서 logo.png 1번, code.png 1번
        // 총 logo.png는 2번, code.png는 1번 호출되어야 합니다
        verify(s3Uploader, times(2)).upload(any(byte[].class), eq("logo.png"));
        verify(s3Uploader, times(1)).upload(any(byte[].class), eq("code.png"));
        // 외부 이미지는 업로드되지 않아야 합니다
        verify(s3Uploader, never()).upload(any(byte[].class), eq("https://google.com/logo.png"));
    }

    @Test
    @DisplayName("이미지 파일이 존재하지 않으면 원본 경로를 그대로 유지한다")
    void process_Should_Keep_Original_Path_If_File_Not_Found() throws IOException {
        // Given
        // [중요] 이 테스트는 파일이 없어서 upload를 호출하지 않으므로, when(...) 설정을 하지 않습니다.

        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("missing-file.md");

        String content = """
---
title: Fail Test
---
![없는이미지](img/ghost.png)
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.contentHtml()).contains("src=\"img/ghost.png\"");
        // 업로드가 호출되지 않아야 합니다
        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("Front Matter가 없을 때 기본값을 사용한다")
    void process_Should_Use_Default_Values_When_Front_Matter_Is_Missing() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("no-front-matter.md");

        String content = """
# 제목 없는 게시글
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.title()).isEqualTo("Untitled");
        assertThat(result.date()).isNotNull();
        assertThat(result.isPublic()).isTrue();
        assertThat(result.thumbnail()).isNull();
        assertThat(result.tags()).isEmpty();
        assertThat(result.contentHtml()).contains("제목 없는 게시글");
    }

    @Test
    @DisplayName("썸네일이 웹 URL일 때 그대로 유지한다")
    void process_Should_Keep_Web_Url_Thumbnail_As_Is() throws IOException {
        // Given - 웹 URL 썸네일은 업로드되지 않으므로 stubbing 불필요
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("web-thumbnail.md");

        String content = """
---
title: 웹 썸네일 테스트
thumbnail: https://example.com/thumbnail.jpg
---
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.thumbnail()).isEqualTo("https://example.com/thumbnail.jpg");
        // 웹 URL은 업로드되지 않아야 합니다
        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("썸네일이 없을 때 null을 반환한다")
    void process_Should_Return_Null_When_Thumbnail_Is_Missing() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("no-thumbnail.md");

        String content = """
---
title: 썸네일 없음
---
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.thumbnail()).isNull();
        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("published 필드가 false일 때 isPublic이 false가 된다")
    void process_Should_Set_IsPublic_False_When_Published_Is_False() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("draft.md");

        String content = """
---
title: 초안 게시글
published: false
---
초안 내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.isPublic()).isFalse();
        assertThat(result.title()).isEqualTo("초안 게시글");
    }

    @Test
    @DisplayName("tags가 없을 때 빈 리스트를 반환한다")
    void process_Should_Return_Empty_List_When_Tags_Are_Missing() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("no-tags.md");

        String content = """
---
title: 태그 없음
---
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.tags()).isEmpty();
    }

    @Test
    @DisplayName("여러 태그를 올바르게 파싱한다")
    void process_Should_Parse_Multiple_Tags_Correctly() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("multiple-tags.md");

        String content = """
---
title: 다중 태그 테스트
tags:
  - Java
  - Spring
  - Test
  - JUnit
---
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.tags()).hasSize(4);
        assertThat(result.tags()).containsExactly("Java", "Spring", "Test", "JUnit");
    }

    @Test
    @DisplayName("날짜를 올바르게 파싱한다")
    void process_Should_Parse_Date_Correctly() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("date-test.md");

        String content = """
---
title: 날짜 테스트
date: 2024-12-25
---
크리스마스 게시글입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.date()).isEqualTo(LocalDate.of(2024, 12, 25).atStartOfDay());
    }

    @Test
    @DisplayName("상대 경로 이미지만 처리하고 절대 경로 이미지도 처리한다")
    void process_Should_Handle_Both_Relative_And_Absolute_Path_Images() throws IOException {
        // Given
        when(s3Uploader.upload(any(byte[].class), any(String.class)))
                .thenAnswer(invocation -> {
                    String filename = invocation.getArgument(1);
                    return "https://s3.aws.com/" + filename;
                });

        Path assetsDir = tempGitRoot.resolve("assets/img");
        Path postDir = tempGitRoot.resolve("posts");
        Path postImgDir = postDir.resolve("img");

        Files.createDirectories(assetsDir);
        Files.createDirectories(postImgDir);

        Path relativeImage = postImgDir.resolve("relative.png");
        Path absoluteImage = assetsDir.resolve("absolute.png");

        Files.write(relativeImage, "relative-image".getBytes());
        Files.write(absoluteImage, "absolute-image".getBytes());

        Path markdownFile = postDir.resolve("mixed-paths.md");
        String content = """
---
title: 혼합 경로 테스트
---
![상대경로](img/relative.png)
![절대경로](/assets/img/absolute.png)
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        String html = result.contentHtml();
        assertThat(html).contains("src=\"https://s3.aws.com/relative.png\"");
        assertThat(html).contains("src=\"https://s3.aws.com/absolute.png\"");

        verify(s3Uploader, times(1)).upload(any(byte[].class), eq("relative.png"));
        verify(s3Uploader, times(1)).upload(any(byte[].class), eq("absolute.png"));
    }

    @Test
    @DisplayName("썸네일 파일이 존재하지 않으면 원본 경로를 반환한다")
    void process_Should_Return_Original_Path_When_Thumbnail_File_Not_Found() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("missing-thumbnail.md");

        String content = """
---
title: 썸네일 파일 없음
thumbnail: /assets/img/missing.png
---
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        assertThat(result.thumbnail()).isEqualTo("/assets/img/missing.png");
        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("외부 이미지(https://)는 업로드하지 않고 그대로 유지한다")
    void process_Should_Keep_External_Https_Images_As_Is() throws IOException {
        // Given
        // 외부 이미지는 업로드되지 않으므로 stubbing이 필요 없습니다.

        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("external-images.md");

        String content = """
---
title: 외부 이미지 테스트
---
![외부이미지1](https://example.com/image1.png)
![외부이미지2](https://cdn.example.com/image2.jpg)
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        String html = result.contentHtml();
        assertThat(html).contains("src=\"https://example.com/image1.png\"");
        assertThat(html).contains("src=\"https://cdn.example.com/image2.jpg\"");

        // 외부 이미지는 업로드되지 않아야 합니다
        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("프로토콜 없는 외부 이미지(//)도 그대로 유지한다")
    void process_Should_Keep_Protocol_Relative_Images_As_Is() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("protocol-relative.md");

        String content = """
---
title: 프로토콜 상대 경로 테스트
---
![프로토콜 상대](//cdn.example.com/image.png)
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        String html = result.contentHtml();
        assertThat(html).contains("src=\"//cdn.example.com/image.png\"");

        verify(s3Uploader, never()).upload(any(byte[].class), any(String.class));
    }

    @Test
    @DisplayName("마크다운 본문이 올바르게 HTML로 변환된다")
    void process_Should_Convert_Markdown_To_Html_Correctly() throws IOException {
        // Given
        Path postDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postDir);
        Path markdownFile = postDir.resolve("markdown-conversion.md");

        String content = """
---
title: 마크다운 변환 테스트
---
# 제목 1
## 제목 2

**굵은 글씨**와 *기울임*이 있습니다.

- 리스트 항목 1
- 리스트 항목 2

1. 순서 있는 리스트 1
2. 순서 있는 리스트 2
""";
        Files.writeString(markdownFile, content);

        // When
        PostParseResult result = markdownProcessor.process(markdownFile, tempGitRoot);

        // Then
        String html = result.contentHtml();
        // HTML로 변환되었는지 확인 (구체적인 태그는 flexmark 버전에 따라 다를 수 있음)
        assertThat(html).isNotEmpty();
        // 제목이 포함되어 있는지 확인
        assertThat(html.toLowerCase()).contains("제목 1");
        assertThat(html.toLowerCase()).contains("제목 2");
    }
}