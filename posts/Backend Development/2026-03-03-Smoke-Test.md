---
title: 🔥 Smoke Test — 시스템의 1차 생존 확인
published: true
tags:
    - Backend
    - Spring Boot
    - Smoke Test
    - Testing
    - DevOps
    - CI/CD
    - MSA
    - Health Check

date: 2026-03-03
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# 🔥 Smoke Test — 시스템의 1차 생존 확인

> **"배포했는데, 서버가 살아있긴 한가?"**
> 스모크 테스트는 이 단 하나의 질문에 답하는 테스트입니다.

---

## 💡 스모크 테스트란?

**Smoke Test**는 시스템이 최소한의 기본 기능을 수행할 수 있는지를 빠르게 확인하는 **1차 생존 테스트**입니다.

이름의 유래는 하드웨어 테스트에서 왔습니다.

> 전원을 켰을 때 **연기(smoke)가 나면** → ❌ 치명적 결함
> **연기 없이 켜지면** → ✅ 최소 통과

소프트웨어에서도 동일한 철학입니다.  
빠르게 배포하고, **"일단 터지지는 않는가"** 를 먼저 확인합니다.

---

## 🏗️ 스모크 테스트가 확인하는 것

| 확인 항목            | 설명                                        |
| -------------------- | ------------------------------------------- |
| ✅ 애플리케이션 기동 | Spring Context가 정상적으로 로드되는가      |
| ✅ Bean 생성         | 주요 Bean이 충돌 없이 생성되는가            |
| ✅ DB 연결           | DataSource 연결이 유효한가                  |
| ✅ 외부 시스템 연결  | Redis, 외부 API 등 연동이 정상인가          |
| ✅ 핵심 API 응답     | 주요 엔드포인트가 500 없이 응답하는가       |
| ✅ 보안 설정         | SecurityConfig가 치명적으로 깨지지 않았는가 |

---

## 🧩 Spring Boot 스모크 테스트 예시

### 1️⃣ Context 기동 확인 (가장 기본)

```java
@SpringBootTest
class ApplicationSmokeTest {

    @Test
    void contextLoads() {
        // Context가 정상 로드되면 통과
    }

}
```

가장 단순하지만 강력합니다.  
Bean 충돌, 설정 오류, DB 연결 실패가 있으면 **이 테스트 하나로 바로 발견**됩니다.

---

### 2️⃣ 핵심 Bean 주입 확인

```java
@SpringBootTest
class BeanSmokeTest {

    @Autowired ApplicationContext context;

    @Test
    void criticalBeansLoaded() {
        assertNotNull(context.getBean(UserService.class));
        assertNotNull(context.getBean(JwtProvider.class));
    }

}
```

핵심 컴포넌트가 실제로 주입되는지 명시적으로 검증합니다.

---

### 3️⃣ API Health Check (CI/CD에서 가장 많이 사용)

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiSmokeTest {

    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void healthCheck() {
        ResponseEntity<String> response =
            restTemplate.getForEntity("/actuator/health", String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
```

배포 직후 CI/CD 파이프라인에서 자동으로 실행하는 대표적인 패턴입니다.

---

### 4️⃣ DB & Redis 연결 확인

```java
@SpringBootTest
class InfrastructureSmokeTest {

    @Autowired DataSource dataSource;
    @Autowired RedisTemplate<String, String> redisTemplate;

    @Test
    void databaseConnectionIsHealthy() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertTrue(connection.isValid(1));
        }
    }

    @Test
    void redisConnectionIsHealthy() {
        redisTemplate.opsForValue().set("smoke:ping", "pong");
        assertEquals("pong", redisTemplate.opsForValue().get("smoke:ping"));
    }

}
```

외부 인프라 연결 문제를 배포 직후 빠르게 잡아낼 수 있습니다.

---

## 🎯 테스트 종류 비교

| 종류                 | 목적                  | 범위      | 속도         |
| -------------------- | --------------------- | --------- | ------------ |
| **Smoke Test**       | 기본 기동 / 생존 확인 | 아주 좁음 | ⚡ 매우 빠름 |
| **Unit Test**        | 개별 로직 검증        | 매우 좁음 | 🔵 빠름      |
| **Integration Test** | 모듈 간 동작 확인     | 중간      | 🟡 느림      |
| **E2E Test**         | 전체 사용자 시나리오  | 넓음      | 🔴 매우 느림 |

> 스모크 테스트는 **가장 먼저, 가장 빠르게** 실행해서 치명적 결함을 조기에 차단합니다.

---

## 🚀 DevOps 파이프라인에서의 위치

<img src="https://github.com/devKobe24/images2/blob/main/smoke-test-1.png?raw=true"/>

스모크 테스트가 실패하면 **배포 자체가 차단**됩니다.  
이후 비용이 큰 통합/E2E 테스트를 실행하기 전에 빠르게 걸러내는 것이 핵심입니다.

---

## 🏢 MSA 환경에서의 스모크 전략

MSA 환경에서는 **서비스 단위**로 스모크 테스트를 구성합니다.  
Spring Boot Actuator를 활용하면 표준화된 헬스체크 엔드포인트를 쉽게 만들 수 있습니다.

```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health, info, ready
  endpoint:
    health:
      show-details: always
```

| 엔드포인트                       | 용도                                               |
| -------------------------------- | -------------------------------------------------- |
| `GET /actuator/health`           | 서비스 전반 상태 확인                              |
| `GET /actuator/health/liveness`  | 프로세스 생존 여부 (Kubernetes liveness probe)     |
| `GET /actuator/health/readiness` | 트래픽 수신 준비 여부 (Kubernetes readiness probe) |

---

## ✅ 체크리스트 — 내 프로젝트에 적용하기

스모크 테스트 도입 시 아래 항목을 순서대로 확인합니다.

```
□ @SpringBootTest contextLoads() 테스트 존재하는가?
□ DB 연결 검증 테스트가 있는가?
□ Redis 등 외부 인프라 연결 검증이 있는가?
□ JWT / Security 설정 오류를 잡는 테스트가 있는가?
□ /actuator/health 엔드포인트가 200을 반환하는가?
□ CI/CD 파이프라인에서 스모크 테스트가 자동 실행되는가?
□ 스모크 테스트 실패 시 배포가 차단되는가?
```

---

## 📌 한 줄 정리

> **스모크 테스트는 "시스템이 최소한 터지지 않고 기본 기능은 작동하는지 확인하는 1차 생존 테스트"이다.**

빠르게 실패하고(Fail Fast), 빠르게 탐지하는 것이 핵심입니다.  
정교한 비즈니스 로직 검증은 통합 테스트와 E2E 테스트에 맡기세요.
