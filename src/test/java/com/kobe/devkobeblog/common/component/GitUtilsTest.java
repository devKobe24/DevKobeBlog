package com.kobe.devkobeblog.common.component;

import org.eclipse.jgit.api.Git;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName    : com.kobe.devkobeblog.common.component
 * fileName       : GitUtilsTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */
class GitUtilsTest {

    private GitUtils gitUtils;

    // JUnit5가 테스트 실행 시마다 임시 폴더를 생성하고 끝나면 삭제해줍니다.
    @TempDir
    Path tempDir;

    private File remoteRepoDir;     // 가짜 원격 저장소 (Github 역할)
    private File localTargetDir;    // 로컬에 다룬로드 받을 경로

    @BeforeEach
    void setUp() throws Exception {
        gitUtils = new GitUtils();

        // 1. 임시 폴더 내에 'remote'와 'local' 디렉토리 경로 설정
        remoteRepoDir = tempDir.resolve("remote-repo").toFile();
        localTargetDir = tempDir.resolve("local-target").toFile();

        // 2. [Remote] 가짜 원격 저장소 초기화 (git init)
        try (Git git = Git.init().setDirectory(remoteRepoDir).call()) {
            // 초기 파일 생성 및 커밋 (main 브랜치 생성)
            File readme = new File(remoteRepoDir, "README.md");
            Files.writeString(readme.toPath(), "# Hello World");

            git.add().addFilepattern(".").call();
            git.commit().setMessage("Initial commit").call();

            // JGit Clone 시 기본 브랜치 이슈 방지를 위해 main 브랜치로 변경
            git.branchRename().setNewName("main").call();
        }

        // 3. GitUtils에 가짜 경로 주입 (@Value 대신)
        // 로컬 파일 경로를 URL로 쓰면 JGit이 알아서 로컬 프로토콜로 인식합니다.
        ReflectionTestUtils.setField(gitUtils, "gitUrl", remoteRepoDir.getAbsolutePath());
        ReflectionTestUtils.setField(gitUtils, "localPathStr", localTargetDir.getAbsolutePath());
    }

    @Test
    @DisplayName("로컬에 저장소가 없으면 Clone을 수행한다")
    void sync_Should_Clone_If_Local_Not_Exists() throws Exception {
        // Given
        // localTargetDir 아직 생성되지 않았거나 비어있음

        // When
        gitUtils.sync();

        // Then
        // 1. .git 폴더가 생겼는지 확인
        assertThat(new File(localTargetDir, ".git")).exists();
        // 2. 원격에 있던 README.md가 잘 받아졌는지 확인
        File localReadme = new File(localTargetDir, "README.md");
        assertThat(localReadme).exists();
        assertThat(Files.readString(localReadme.toPath())).contains("# Hello World");
    }

    @Test
    @DisplayName("로털에 저장소가 이미 있으면 Pull을 수행하여 최신 내용을 가져온다")
    void sync_Should_Pull_If_Local_Exists() throws Exception {
        // Given
        // 1. 먼저 Clone을 해서 로컬 저장소를 만들어 둡니다.
        gitUtils.sync();

        // 2. [Remote] 원격 저장소에 새로운 파일 추가 (Update 상황 연출)
        try (Git remoteGit = Git.open(remoteRepoDir)) {
            File newPost = new File(remoteRepoDir, "new-post.md");
            Files.writeString(newPost.toPath(), "New Post Content");

            remoteGit.add().addFilepattern(".").call();
            remoteGit.commit().setMessage("Add new post").call();
        }

        // When
        // 다시 sync 호출 (이번엔 Pull이 동작해야 함)
        gitUtils.sync();

        // Then
        // 로컬에 새 파일이 들어왔는지 확인
        File localNewPost = new File(localTargetDir, "new-post.md");
        assertThat(localNewPost).exists();
        assertThat(Files.readString(localNewPost.toPath())).isEqualTo("New Post Content");
    }

    @Test
    @DisplayName("로컬 폴더가 Git 저장소가 아니거나 훼손된 경우 삭제 후 다시 Clone 한다")
    void sync_Should_ReClone_If_Local_Is_Invalid() throws Exception {
        // Given
        // 1. 로컬 폴더를 일반 디렉토리로 만듦 (Git 저장소 아님)
        if (!localTargetDir.exists()) {
            localTargetDir.mkdirs();
        }
        File garbageFile = new File(localTargetDir, "garbage.txt");
        Files.writeString(garbageFile.toPath(), "This is not a git repo");

        // When
        gitUtils.sync();

        // Then
        // 1. 엉뚱한 파일은 삭제되고
        assertThat(garbageFile).doesNotExist();
        // 2. 정상적인 Git 저장소로 복구되었는지 확인
        assertThat(new File(localTargetDir, ".git")).exists();
        assertThat(new File(localTargetDir, "README.md")).exists();
    }
}