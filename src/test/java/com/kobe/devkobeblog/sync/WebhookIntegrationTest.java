package com.kobe.devkobeblog.sync;

import com.kobe.devkobeblog.common.component.GitUtils;
import com.kobe.devkobeblog.common.component.S3Uploader;
import com.kobe.devkobeblog.post.domain.Post;
import com.kobe.devkobeblog.post.domain.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Formatter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.kobe.devkobeblog.sync
 * fileName       : WebhookIntegrationTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 28.
 * description    : 통합 테스트(Integration Test), 전체 시스템의 흐름(Flow)이 유기적으로 잘 동작하는지 검증하는 단계입니다.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 28.        kobe / Minsung Kang       최초 생성
 */
@SpringBootTest(
        properties = {
                "blog.git.webhook-secret=test-integration-secret", // 테스트용 시크릿 키 설정
                "blog.git.local-path=./temp-git", // GitUtils가 Mock이라 실제로는 안 쓰이지만 설정 필요
                // S3Config가 요구하는 AWS 프로퍼티 (테스트용 더미 값)
                "cloud.aws.credentials.access-key=test-access-key",
                "cloud.aws.credentials.secret-key=test-secret-key",
                "cloud.aws.region.static=ap-northeast-2",
                "cloud.aws.s3.bucket=test-bucket"
        }
)
@AutoConfigureMockMvc // MockMvc 사용
@ActiveProfiles("test") // application-test.yml (H2 DB) 사용
public class WebhookIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PostRepository postRepository;

    // 외부 시스템은 Mock 처리 (실제 동작 X)
    @MockBean
    private GitUtils gitUtils;

    @MockBean
    private S3Uploader s3Uploader;

    @TempDir
    Path tempGitRoot;

    private static final String TEST_SECRET = "test-integration-secret";

    @Test
    @DisplayName("[통합] 웹훅 요청이 오면 파일 시스템을 읽어 DB에 저장한다 (End-to-End)")
    void webhook_Should_Trigger_Service_And_Save_To_DB() throws Exception {
        // ==========================================
        // 1. Given: 가짜 Git 환경 및 파일 생성
        // ==========================================
        Path postsDir = tempGitRoot.resolve("posts/java");
        Files.createDirectories(postsDir);

        // 썸네일 이미지 파일 생성 (S3 업로드가 호출되도록)
        Path assetsDir = tempGitRoot.resolve("assets/img");
        Files.createDirectories(assetsDir);
        Path thumbFile = assetsDir.resolve("thumb.png");
        Files.write(thumbFile, "dummy-thumbnail-bytes".getBytes());

        Path markdownFile = postsDir.resolve("integration-test.md");
        String content = """
---
title: 통합 테스트 게시글
date: 2024-01-01
thumbnail: /assets/img/thumb.png
---
# 통합 테스트 중
내용입니다.
""";
        Files.writeString(markdownFile, content);

        // Mock: GitUtils가 sync() 호출 시 위에서 만든 tempGitRoot를 반환하도록 설정
        when(gitUtils.sync()).thenReturn(tempGitRoot);

        // Mock: S3Uploader는 무조건 가짜 URL 반환
        when(s3Uploader.upload(any(), any())).thenReturn("https://s3.fake/thumb.png");


        // ==========================================
        // 2. When: 컨트롤러에 웹훅 요청 전송
        // ==========================================
        String payload = "{\"ref\": \"refs/heads/main\"}";
        String signature = "sha256=" + hmacSha256(payload, TEST_SECRET);

        mockMvc.perform(post("/api/webhook/sync")
                        .header("X-Hub-Signature-256", signature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isOk())
                .andExpect(content().string("Sync started"));


        // ==========================================
        // 3. Then: DB 반영 확인 (비동기 처리 대기)
        // ==========================================

        // 중요: 서비스 로직이 @Async 스레드에서 돌기 때문에 즉시 확인하면 DB가 비어있을 수 있음.
        // Awaitility를 사용하여 최대 2초간 DB에 데이터가 들어오기를 기다림.
        await().atMost(2, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Post> posts = postRepository.findAll();
            assertThat(posts).hasSize(1);

            Post savedPost = posts.get(0);
            assertThat(savedPost.getTitle()).isEqualTo("통합 테스트 게시글");
            assertThat(savedPost.getThumbnail()).isEqualTo("https://s3.fake/thumb.png");
            assertThat(savedPost.getStatus().name()).isEqualTo("PUBLIC");
        });
    }

    @Test
    @DisplayName("[통합] 서명이 잘못되면 서비스가 실행되지 않고 DB도 변하지 않는다")
    void webhook_Should_Fail_With_Invalid_Signature() throws Exception {
        // Given
        String payload = "{}";
        String invalidSignature = "sha256=invalid";

        // When
        mockMvc.perform(post("/api/webhook/sync")
                .header("X-Hub-Signature-256", invalidSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isUnauthorized());

        // Then
        // 시간이 지나도 DB는 비어있어야 함
        Thread.sleep(500);
        assertThat(postRepository.count()).isEqualTo(0);
    }

    private String hmacSha256(String data, String key) throws Exception {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKeySpec);
        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return toHexString(bytes);
    }

    private String toHexString(byte[] bytes) {
        try (Formatter formatter = new Formatter()) {
            for (byte b : bytes) {
                formatter.format("%02x", b);
            }
            return formatter.toString();
        }
    }
}
