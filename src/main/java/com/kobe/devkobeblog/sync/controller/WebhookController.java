package com.kobe.devkobeblog.sync.controller;

import com.kobe.devkobeblog.post.service.PostSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

/**
 * packageName    : com.kobe.devkobeblog.sync.controller
 * fileName       : WebhookController
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class WebhookController {

    private final PostSyncService postSyncService;

    @Value("${blog.git.webhook-secret}")
    private String webhookSecret;

    @PostMapping("/api/webhook/sync")
    public ResponseEntity<String> handleGitWebhook(
            @RequestHeader(value = "X-Hub-Signature-256", required = false) String signature,
            @RequestBody String payload) {

        // 1. 보안 검증: Github에서 보낸 요청이 맞는지 서명 확인
        if (!verifySignature(payload, signature)) {
            log.warn("Invalid Github signature detected.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid Signature");
        }

        log.info("Github Webhook received. Triggering sync...");

        // 2. 비동기 작업 실행 (응답은 바로 나감)
        triggerAsyncSync();

        return ResponseEntity.ok("Sync started");
    }

    private void triggerAsyncSync() {
        postSyncService.syncPosts();
    }

    /**
     * Github 서명 검증 로직 (HMAC SHA-256)
     */
    private boolean verifySignature(String payload, String signature) {
        if (signature == null || !signature.startsWith("sha256=")) {
            return false;
        }

        // 개발 환경 등에서 Secret 설정을 안 했을 경우 통과시킬지 여부 결정 (보안상 막는 게 좋음)
        if (webhookSecret == null || webhookSecret.isBlank()) {
            log.error("Webhook secret is not configured on server.");
            return false;
        }

        try {
            String cleanSignature = signature.substring(7); // "sha256=" 제거
            String computedHash = hmacSha256(payload, webhookSecret);

            // 시간차 공격 방지를 위한 constant-time 비교가 좋지만, 여기선 equals로 처리
            return computedHash.equals(cleanSignature);
        } catch (Exception e) {
            log.error("Signature verification failed.", e);
            return false;
        }
    }

    private String hmacSha256(String data, String key) throws NoSuchAlgorithmException, InvalidKeyException {
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
