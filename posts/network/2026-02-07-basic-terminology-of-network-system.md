---
title: 🌐 네트워크 시스템 기초 용어
published: true
tags:
    - Backend
    - Network
    - Terminology
    
date: 2026-02-07
thumbnail: /assets/img/thumbnail/network.jpg
---

# 🌐 네트워크 시스템 기초 용어

> Backend 개발자가 꼭 알아야 할 네트워크 개념 정리

## 📊 시스템 계층 구조 한눈에 보기

```
노드 (Node) - 가장 포괄적인 개념
├── 라우터 (Router) - 인터넷 내부
└── 호스트 (Host) - 인터넷 외부
    ├── 서버 (Server) - 서비스 제공자
    └── 클라이언트 (Client) - 서비스 사용자
```

---

## 🔍 핵심 용어 정리

### 1️⃣ 노드 (Node)

**정의**: 네트워크에 연결된 모든 시스템의 통칭

**특징:**

- 📡 데이터를 주고받을 수 있는 모든 장치
- 💻 컴퓨팅 기능과 데이터 전송 기능 보유
- 🌐 인터넷의 모든 구성요소를 포괄

**종류:**

```
노드
├── 라우터: 인터넷 내부 (데이터 중개)
└── 호스트: 인터넷 외부 (서비스 제공/사용)
```

---

### 2️⃣ 라우터 (Router)

**정의**: 인터넷 내부에서 데이터를 중개하는 시스템

**역할:**

```
호스트 A → [라우터 1] → [라우터 2] → [라우터 3] → 호스트 B
            최적 경로 탐색 및 데이터 전달
```

**핵심 기능:**

- 🧭 **경로 설정**: 최적의 데이터 전송 경로 결정
- 🔄 **데이터 중개**: 호스트 간 데이터 전달
- 📊 **트래픽 관리**: 네트워크 흐름 제어

**개발자 관점:**

```java
// 라우터의 역할을 코드로 이해하면
@Configuration
public class RoutingConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r -> r
                .path("/api/users/**")
                .uri("http://user-service:8081"))  // 라우팅
            .route("order-service", r -> r
                .path("/api/orders/**")
                .uri("http://order-service:8082"))  // 라우팅
            .build();
    }
}
```

---

### 3️⃣ 호스트 (Host)

**정의**: 인터넷 외부에 연결되어 사용자의 접속 창구 역할을 하는 시스템

**특징:**

- 🖥️ 일반 컴퓨팅 기능 보유
- 📱 네트워크 애플리케이션 실행 가능
- 🔌 사용자가 직접 접근하는 시스템

**역할에 따른 분류:**

```
호스트
├── 서버: 서비스 제공
└── 클라이언트: 서비스 이용
```

**예시:**

- 💻 개인 PC, 노트북
- 📱 스마트폰, 태블릿
- 🖥️ 서버 컴퓨터
- 🎮 게임 콘솔

---

### 4️⃣ 서버 (Server)

**정의**: 서비스를 제공하는 시스템 또는 프로그램

**핵심 특징:**

```
1. 먼저 실행 (클라이언트보다 먼저 대기)
2. 항상 실행 (영구적 실행 상태)
3. 요청 대기 (클라이언트 요청 수신 대기)
4. 서비스 제공 (요청에 대한 응답 반복)
```

**실행 순서:**

```
1. 서버 시작 → 2. 포트 바인딩 → 3. 클라이언트 대기 → 4. 요청 처리 → 3번으로 반복
```

**Spring Boot 서버 예제:**

```java
@RestController
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    // 서버: 서비스 제공자
    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        User user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/users")
    public ResponseEntity<User> createUser(@RequestBody UserRequest request) {
        User user = userService.create(request);
        return ResponseEntity.created(
            URI.create("/api/users/" + user.getId())
        ).body(user);
    }
}

// 애플리케이션 시작 (서버 역할 시작)
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);
        // 포트 8080에서 요청 대기 중...
    }
}
```

---

### 5️⃣ 클라이언트 (Client)

**정의**: 서비스를 이용하는 시스템 또는 프로그램

**핵심 특징:**

```
1. 필요시 실행 (서비스가 필요할 때만)
2. 요청 발신 (서버에 서비스 요청)
3. 응답 수신 (서버로부터 결과 받음)
4. 종료 가능 (작업 완료 후 종료)
```

**실행 순서:**

```
1. 클라이언트 시작 → 2. 서버 연결 → 3. 요청 전송 → 4. 응답 수신 → 5. 종료
```

**Java 클라이언트 예제:**

```java
@Service
public class UserApiClient {

    private final RestTemplate restTemplate;

    // 클라이언트: 서비스 사용자
    public User getUser(Long id) {
        String url = "http://api-server:8080/api/users/" + id;

        // 서버에 GET 요청
        ResponseEntity<User> response = restTemplate.getForEntity(
            url,
            User.class
        );

        return response.getBody();
    }

    public User createUser(UserRequest request) {
        String url = "http://api-server:8080/api/users";

        // 서버에 POST 요청
        ResponseEntity<User> response = restTemplate.postForEntity(
            url,
            request,
            User.class
        );

        return response.getBody();
    }
}
```

---

## 🔄 클라이언트와 서버의 상대적 관계

### 핵심 개념

> **클라이언트와 서버는 절대적 역할이 아닌, 서비스 단위로 결정되는 상대적 역할!**

### 실전 예제: 마이크로서비스 아키텍처

```
┌─────────────────────────────────────────────────┐
│              사용자 (웹 브라우저)                 │
│                   클라이언트                      │
└────────────┬────────────────────────────────────┘
             │ HTTP Request
             ↓
┌─────────────────────────────────────────────────┐
│            API Gateway (포트 8080)               │
│    서비스 1: 클라이언트 (사용자 요청 받음)        │
│    서비스 2: 서버 (사용자에게 응답)              │
└────────────┬────────────────────────────────────┘
             │ 내부 API 호출
             ↓
┌─────────────────────────────────────────────────┐
│          User Service (포트 8081)                │
│    서비스 1: 서버 (Gateway 요청 처리)            │
│    서비스 2: 클라이언트 (DB 접근)                │
└────────────┬────────────────────────────────────┘
             │ Query
             ↓
┌─────────────────────────────────────────────────┐
│              Database (포트 3306)                │
│                   서버                            │
└─────────────────────────────────────────────────┘
```

### 코드로 이해하기

```java
// API Gateway - 이중 역할
@RestController
public class GatewayController {

    private final UserServiceClient userServiceClient;

    @GetMapping("/users/{id}")
    public ResponseEntity<User> getUser(@PathVariable Long id) {
        // 1. 웹 브라우저에 대해서는 "서버" 역할
        // 2. User Service에 대해서는 "클라이언트" 역할

        User user = userServiceClient.getUser(id);  // 클라이언트로 동작
        return ResponseEntity.ok(user);  // 서버로 동작
    }
}

// User Service - 이중 역할
@Service
public class UserService {

    private final UserRepository userRepository;

    public User findById(Long id) {
        // 1. API Gateway에 대해서는 "서버" 역할
        // 2. Database에 대해서는 "클라이언트" 역할

        return userRepository.findById(id)  // 클라이언트로 동작
            .orElseThrow(() -> new UserNotFoundException(id));
    }
}
```

---

## 📋 실무 시나리오

### 시나리오 1: 파일 전송 (FTP)

```
호스트 A (클라이언트)
    ↓ FTP 요청: "파일 다운로드"
호스트 B (서버)
    ↓ FTP 응답: 파일 전송
호스트 A (클라이언트)
```

**코드 예제:**

```java
// 호스트 A - FTP 클라이언트
public class FtpClient {
    public void downloadFile(String filename) {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect("ftp-server.com", 21);
        ftpClient.login("user", "password");

        // 서버에 파일 요청
        InputStream inputStream = ftpClient.retrieveFileStream(filename);
        // 파일 다운로드...
    }
}
```

### 시나리오 2: 원격 접속 (Telnet/SSH)

```
호스트 A
    ↓ Telnet 요청: "로그인"
호스트 B (서버)
    ↓ 세션 생성
호스트 A (클라이언트)
```

### 시나리오 3: REST API 통신

```
Frontend (React)
    ↓ GET /api/products
Backend (Spring Boot) - 서버
    ↓ SELECT * FROM products
Database (MySQL) - 서버
    ↓ 결과 반환
Backend (Spring Boot) - 클라이언트
    ↓ JSON 응답
Frontend (React) - 클라이언트
```

---

## 🎯 서버의 특성

### 영구 실행 서버

```java
@SpringBootApplication
public class ServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServerApplication.class, args);

        // 프로그램이 종료되지 않고 계속 실행
        // 클라이언트 요청을 무한정 대기
    }
}
```

### 요청 처리 흐름

```java
@RestController
public class ProductController {

    // 서버는 항상 대기 중
    @GetMapping("/products")
    public List<Product> getProducts() {
        // 1. 요청 수신
        // 2. 비즈니스 로직 처리
        // 3. 응답 반환
        // 4. 다시 대기 상태로 (무한 반복)
        return productService.getAllProducts();
    }
}
```

---

## 💡 용어 정리표

| 용어           | 역할        | 위치        | 주요 기능         | 실무 예시               |
| -------------- | ----------- | ----------- | ----------------- | ----------------------- |
| **노드**       | 포괄 개념   | 전체        | 데이터 송수신     | 모든 네트워크 장비      |
| **라우터**     | 중개자      | 인터넷 내부 | 경로 설정         | API Gateway             |
| **호스트**     | 종단 시스템 | 인터넷 외부 | 서비스 제공/이용  | 서버, PC, 스마트폰      |
| **서버**       | 서비스 제공 | 호스트      | 요청 대기 및 처리 | Spring Boot App         |
| **클라이언트** | 서비스 이용 | 호스트      | 요청 발신         | React App, RestTemplate |

---

## 🏗️ MSA에서의 역할 변화

### 전통적 아키텍처

```
클라이언트 (웹) ↔ 서버 (모놀리스)
```

### 마이크로서비스 아키텍처

```
클라이언트 (웹)
    ↓
API Gateway (서버이면서 클라이언트)
    ├→ User Service (서버이면서 클라이언트)
    ├→ Order Service (서버이면서 클라이언트)
    └→ Product Service (서버이면서 클라이언트)
         ↓
    Database (서버)
```

**핵심 포인트:**

- 🔄 각 서비스는 다른 서비스에 대해 클라이언트
- 📡 각 서비스는 요청자에 대해 서버
- 🎭 **역할은 상대적이고 동적으로 변함**

---

## 📚 실무 적용 팁

### 1️⃣ 로그에서 역할 구분하기

```java
@Slf4j
@Service
public class OrderService {

    private final PaymentClient paymentClient;

    public Order createOrder(OrderRequest request) {
        log.info("[SERVER] 주문 생성 요청 수신: {}", request);

        // 클라이언트로서 결제 서비스 호출
        log.info("[CLIENT] 결제 서비스 호출 시작");
        PaymentResponse payment = paymentClient.processPayment(
            request.getPaymentInfo()
        );
        log.info("[CLIENT] 결제 서비스 호출 완료: {}", payment);

        // 서버로서 응답 반환
        Order order = saveOrder(request, payment);
        log.info("[SERVER] 주문 생성 완료: {}", order.getId());

        return order;
    }
}
```

### 2️⃣ 에러 처리 시 역할 고려

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 서버로서의 에러 (클라이언트에게 반환)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(
        ResourceNotFoundException ex
    ) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse("Resource not found"));
    }

    // 클라이언트로서의 에러 (외부 서비스 호출 실패)
    @ExceptionHandler(RestClientException.class)
    public ResponseEntity<ErrorResponse> handleClientError(
        RestClientException ex
    ) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
            .body(new ErrorResponse("External service unavailable"));
    }
}
```

---

## 🎯 핵심 요약

### 기억해야 할 3가지

1. **계층적 구조**

   ```
   노드 > 호스트 > 서버/클라이언트
   ```

2. **상대적 역할**

   ```
   클라이언트 ↔ 서버 (서비스마다 다름)
   ```

3. **서버의 특성**
   ```
   먼저 실행 → 영구 대기 → 요청 처리 → 반복
   ```

---

## 💬 마무리

> **"모든 시스템은 클라이언트이면서 동시에 서버가 될 수 있다"**

Backend 개발자라면:

- 🎯 API를 설계할 때 서버 역할 이해
- 🔄 외부 API 호출 시 클라이언트 역할 이해
- 🏗️ MSA 구조에서 이중 역할 인식
- 📊 로그와 모니터링 시 역할 구분

네트워크 기본 용어를 이해하면 시스템 설계와 디버깅이 훨씬 쉬워집니다! 💪
