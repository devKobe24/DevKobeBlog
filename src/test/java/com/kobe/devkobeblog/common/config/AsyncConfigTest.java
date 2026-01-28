package com.kobe.devkobeblog.common.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.Executor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * packageName    : com.kobe.devkobeblog.common.config
 * fileName       : AsyncConfigTest
 * author         : kobe / Minsung Kang
 * date           : 2026. 1. 27.
 * description    :
 * ===========================================================
 * DATE              AUTHOR             NOTE
 * -----------------------------------------------------------
 * 2026. 1. 27.        kobe / Minsung Kang       최초 생성
 */

// AsyncConfig 클래스만 단독으로 로드하여 테스트 (가볍고 빠름)
@SpringJUnitConfig(AsyncConfig.class)
class AsyncConfigTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    @Qualifier("syncTaskExecutor") // Bean 이름으로 주입 시도
    private Executor executor;

    @Test
    @DisplayName("syncTaskExecutor 빈이 등록되어야 하며, 설정값이 올바르게 적용되어야 한다")
    void syncTaskExecutor_Should_Be_Configured_Correctly() {
        // 1. Bean 등록 여부 및 타입 확인
        assertThat(executor).isNotNull();
        assertThat(executor).isInstanceOf(ThreadPoolTaskExecutor.class);

        // Bean 이름 확인
        boolean beanExists = applicationContext.containsBean("syncTaskExecutor");
        assertThat(beanExists).isTrue();

        // 2. 설정값 검증
        ThreadPoolTaskExecutor taskExecutor = (ThreadPoolTaskExecutor) executor;

        // CorePoolSize: 1
        assertThat(taskExecutor.getCorePoolSize()).isEqualTo(1);

        // MaxPoolSize: 1
        assertThat(taskExecutor.getMaxPoolSize()).isEqualTo(1);

        // ThreadNamePrefix: "GitSync-"
        assertThat(taskExecutor.getThreadNamePrefix()).isEqualTo("GitSync-");

        // QueueCapacity: 10
        // (getQueueCapacity() 메서드가 없으므로 ReflectionTestUtils로 내부 필드 확인)
        int queueCapacity = (int) ReflectionTestUtils.getField(taskExecutor, "queueCapacity");
        assertThat(queueCapacity).isEqualTo(10);
    }
}