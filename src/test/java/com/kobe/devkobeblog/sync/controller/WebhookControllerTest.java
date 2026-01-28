package com.kobe.devkobeblog.sync.controller;

import com.kobe.devkobeblog.post.service.PostSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Formatter;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * packageName    : com.kobe.devkobeblog.sync.controller
 * fileName       : WebhookControllerTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    : Standalone MockMvc 사용. Spring 컨텍스트(JPA 등) 로드 없이 WebhookController만 단위 테스트.
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@ExtendWith(MockitoExtension.class)
class WebhookControllerTest {

    @Mock
    private PostSyncService postSyncService;

    @InjectMocks
    private WebhookController webhookController;

    private MockMvc mockMvc;

    private static final String TEST_SECRET = "test-secret-key";

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(webhookController, "webhookSecret", TEST_SECRET);
        mockMvc = MockMvcBuilders.standaloneSetup(webhookController).build();
    }

    @Test
    @DisplayName("유효한 서명(Signature)을 가진 요청이 오면 200 OK를 응답하고 Sync 서비스를 호출한다")
    void handleGitWebhook_Should_Return_Ok_With_Valid_Signature() throws Exception {
        String payload = "{\"ref\": \"refs/heads/main\", \"commits\": []}";
        String validSignature = "sha256=" + hmacSha256(payload, TEST_SECRET);

        doNothing().when(postSyncService).syncPosts();

        mockMvc.perform(post("/api/webhook/sync")
                        .header("X-Hub-Signature-256", validSignature)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("Sync started"));

        verify(postSyncService).syncPosts();
    }

    @Test
    @DisplayName("서명이 틀리면 401 Unauthorized를 응답하고 서비스는 호출되지 않는다")
    void handleGitWebhook_Should_Return_Unauthorized_With_Invalid_Signature() throws Exception {
        // Given
        String payload = "{\"ref\": \"refs/heads/main\", \"commits\": []}";
        String invalidSignature = "sha256=" + "invalid_hash_value";

        // When & Then
        mockMvc.perform(post("/api/webhook/sync")
                .header("X-Hub-Signature-256", invalidSignature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andExpect(content().string("Invalid Signature"));

        // Verify: 서비스가 절대 호출되지 않아야 함.
        verify(postSyncService, never()).syncPosts();
    }

    @Test
    @DisplayName("서명 헤더가 아예 없으면 401 Unauthorized를 응답한다")
    void handleGitWebhook_Should_Return_Unauthorized_When_Header_Missing() throws Exception {
        // Given
        String payload = "{}";

        // When & Then
        mockMvc.perform(post("/api/webhook/sync")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload)) // 헤더 누락
                .andExpect(status().isUnauthorized());

        verify(postSyncService, never()).syncPosts();
    }

    @Test
    @DisplayName("Payload 내용이 바뀌면 서명 검증도 실패해야 한다")
    void handleGitWebhook_Should_Fail_If_Payload_Tampered() throws Exception {
        // Given
        String originalPayload = "original";
        String tamperedPayload = "tampered";

        // 서명은 original로 만들고
        String signature = "sha256=" + hmacSha256(originalPayload, TEST_SECRET);

        // 요청은 tampered로 보냄 (해커가 가로채서 내용을 바꾼 상황)
        mockMvc.perform(post("/api/webhook//sync")
                .header("X-Hub-Signature-256", signature)
                .contentType(MediaType.APPLICATION_JSON)
                .content(tamperedPayload))
                .andExpect(status().isUnauthorized());

        verify(postSyncService, never()).syncPosts();
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
