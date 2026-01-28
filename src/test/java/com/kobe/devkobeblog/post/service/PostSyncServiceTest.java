package com.kobe.devkobeblog.post.service;

import com.kobe.devkobeblog.common.component.GitUtils;
import com.kobe.devkobeblog.common.component.MarkdownProcessor;
import com.kobe.devkobeblog.post.domain.Post;
import com.kobe.devkobeblog.post.domain.PostRepository;
import com.kobe.devkobeblog.post.domain.PostStatus;
import com.kobe.devkobeblog.post.dto.PostParseResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * packageName    : com.kobe.devkobeblog.post.service
 * fileName       : PostSyncServiceTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@ExtendWith(MockitoExtension.class)
class PostSyncServiceTest {

    @Mock
    private GitUtils gitUtils;

    @Mock
    private MarkdownProcessor markdownProcessor;

    @Mock
    private PostRepository postRepository;

    @InjectMocks
    private PostSyncService postSyncService;

    @TempDir
    Path tempGitRoot;

    @Test
    @DisplayName("새로운 마크다운 파일이 발견되면 DB에 Insert 한다")
    void voidPosts_Should_Insert_New_Post() throws Exception {
        // Given
        // 1. 가짜 Git 환경 구성
        Path postsDir = tempGitRoot.resolve("posts/java");
        Files.createDirectories(postsDir);
        Path newFile = postsDir.resolve("new-post.md");
        Files.writeString(newFile, "content");

        // 2. Mock 설정
        when(gitUtils.sync()).thenReturn(tempGitRoot);

        // MarkdownProcessor가 리턴할 가짜 파싱 결과
        PostParseResult parseResult = new PostParseResult(
                "New Title", "<h1>Html</h1>", LocalDateTime.now(), true, "thumb.png", List.of("Java")
        );
        when(markdownProcessor.process(eq(newFile), eq(tempGitRoot))).thenReturn(parseResult);

        // DB에 해당 파일 경로로 저장된 글이 없음 (Insert 시나리오)
        String expectedRelativePath = "posts/java/new-post.md"; // OS에 따라 슬래시 다를 수 있음 주의
        when(postRepository.findByFilePath(anyString())).thenReturn(Optional.empty());

        // When
        postSyncService.syncPosts();

        // Then
        // save 메서드가 호출되었는지 검증
        verify(postRepository, times(1)).save(argThat(post ->
                post.getTitle().equals("New Title") &&
                        post.getStatus() == PostStatus.PUBLIC &&
                        post.getFilePath().endsWith("new-post.md") // 경로 구분자 이슈 방지 위해 endsWith 사용
        ));
    }

    @Test
    @DisplayName("이미 존재하는 파일은 DB 내용을 Update 한다")
    void syncPosts_Should_Update_Existing_Post() throws Exception {
        // Given
        Path postsDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postsDir);
        Path existingFile = postsDir.resolve("update-post.md");
        Files.writeString(existingFile, "content");

        when(gitUtils.sync()).thenReturn(tempGitRoot);

        // 수정된 내용의 파싱 결과
        PostParseResult updateResult = new PostParseResult(
                "Updated Title", "<h1>Updated</h1>", LocalDateTime.now(), false, null, List.of()
        );
        when(markdownProcessor.process(any(Path.class), any(Path.class))).thenReturn(updateResult);

        // 기존 DB 데이터 (Mock 객체 대신 실제 객체 사용 -> 상태 변경 확인 용이)
        // 주의: Post 엔티티는 protected 생성자이므로 Builder 사용
        Post existingPost = Post.builder()
                .title("Old Title")
                .filePath("posts/update-post.md")
                .status(PostStatus.PUBLIC)
                .build();

        // Mocking: DB에서 조회 시 위 객체를 리턴
        when(postRepository.findByFilePath(anyString())).thenReturn(Optional.of(existingPost));

        // When
        postSyncService.syncPosts();

        // Then
        // save는 호출되지 않아야 함 (JPA 변경 감지 혹은 명시적 save 호출 여부에 따라 다름, 현재 로직은 save 호출 안함)
        // 대신 existingPost 객체의 내용이 바뀌었는지 확인 (Dirty Checking)
        assert existingPost.getTitle().equals("Updated Title");
        assert existingPost.getStatus() == PostStatus.PRIVATE; // UpdatedResult가 false였으므로
    }

    @Test
    @DisplayName("Git에서 사라진 파일은 DB에서 Soft Delete 처리한다")
    void syncPosts_Should_Soft_Delete_Removed_Post() throws Exception {
        // Given
        // 1. Git 폴더는 비어있음 (파일 없음)
        Path postsDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postsDir);

        when(gitUtils.sync()).thenReturn(tempGitRoot);

        // 2. DB에는 'deleted.md'라는 글이 살아있음(PUBLID)
        Post activePost = mock(Post.class); // delete() 호출 여부 확인을 위해 Mock 객체 사용
        when(activePost.getFilePath()).thenReturn("posts/deleted.md");
        when(activePost.getTitle()).thenReturn("To Be Deleted");

        // findAllByStatusNot(DELETED) 호출 시 이 글을 리턴
        when(postRepository.findAllByStatusNot(PostStatus.DELETED)).thenReturn(List.of(activePost));

        // When
        postSyncService.syncPosts();

        // Then
        // Git 폴더에 해당 파일이 없으므로, delete() 메서드가 호출되어야 함
        verify(activePost, times(1)).delete();
    }

    @Test
    @DisplayName("특정 파일 파싱 실패 시 로그를 남기고 다음 파일을 계속 처리한다")
    void syncPosts_Should_Continue_On_Parsing_Error() throws Exception {
        // Given
        Path postsDir = tempGitRoot.resolve("posts");
        Files.createDirectories(postsDir);

        Path errorFile = postsDir.resolve("error.md");
        Path normalFile = postsDir.resolve("normal.md");
        Files.writeString(errorFile, "error");
        Files.writeString(normalFile, "normal");

        when(gitUtils.sync()).thenReturn(tempGitRoot);

        // errorFile 처리 시 예외 발생 설정
        doThrow(new RuntimeException("Parsing Failed"))
                .when(markdownProcessor).process(eq(errorFile), any());

        // normalFile은 정상 처리
        PostParseResult normalResult = new PostParseResult(
                "Normal", "html", LocalDateTime.now(), true, null, Collections.emptyList());
        doReturn(normalResult).when(markdownProcessor).process(eq(normalFile), any());

        // When
        postSyncService.syncPosts();

        // Then
        // 예외가 발생했지만 서비스가 죽지 않고 normalFile 처리를 시도했는지 검증
        verify(postRepository, times(1)).save(argThat(p -> p.getTitle().equals("Normal")));
    }
}