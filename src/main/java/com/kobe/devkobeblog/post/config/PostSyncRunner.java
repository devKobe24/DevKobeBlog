package com.kobe.devkobeblog.post.config;

import com.kobe.devkobeblog.post.service.PostSyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 앱 시작 시 Git 저장소에서 게시글을 초기 동기화합니다.
 * - 최초 배포/재시작 시 DB에 글이 없던 문제 해결
 * - Webhook은 push 시 실시간 반영용으로 계속 사용
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class PostSyncRunner {

    private final PostSyncService postSyncService;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        log.info("Application ready. Triggering initial post sync from Git...");
        postSyncService.syncPosts();
    }
}
