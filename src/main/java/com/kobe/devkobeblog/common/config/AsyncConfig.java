package com.kobe.devkobeblog.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * packageName    : com.kobe.devkobeblog.common.config
 * fileName       : AsyncConfig
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    : 비동기 설정 활성화
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

@Configuration
@EnableAsync        // 비동기 기능 활성화
public class AsyncConfig {

    @Bean(name = "syncTaskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(1);    // 기본 스레드 1개 (동기화는 순차적으로 하는 게 안전함)
        executor.setMaxPoolSize(1);     // 최대 스레드도 1개
        executor.setQueueCapacity(10);  // 대기열
        executor.setThreadNamePrefix("GitSync-");
        executor.initialize();
        return executor;
    }
}
