---
title: 💉 Spring @Value 완벽 가이드
published: true
tags:
  - Backend Development
  - Server
  - Java
  - Spring Boot

date: 2026-01-29
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# 💉 Spring @Value 완벽 가이드

> 설정 값을 코드에 주입하는 마법! 외부 설정을 우아하게 관리하는 방법

---

## 📋 목차

1. [@Value가 뭔가요?](#What-Is-Value)
2. [언제 사용해야 할까?](#When-Should-It-Be-Used)
3. [주입 가능한 값의 종류](#Types-of-Values-That-Can-Be-Injected)
4. [주입 방식 비교](#Comparison-of-Injection-Methods)
5. [절대 사용하면 안 되는 경우](#Cases-Where-It-Should-Never-Be-Used)
6. [@Value vs @ConfigurationProperties](#value-vs-configurationproperties)
7. [실무 패턴과 Best Practices](#In-thefield-pattern-and-best-practices)
8. [흔한 실수와 해결책](#common-mistake-and-solution)
9. [테스트 전략](#test-strategy)
10. [핵심 요약](#core-summary)

---
<a id="What-Is-Value"></a>
## 1️⃣ @Value가 뭔가요?

**@Value**는 Spring이 관리하는 값(property, 환경변수, 상수, 표현식 등)을 필드·생성자·메서드 파라미터에 주입해주는 애노테이션입니다.

### 💡 한 문장 정의

> "설정 파일이나 환경에 있는 값을 코드에 꽂아 넣어주는 역할"

### 🎯 핵심 개념

```java
// application.yml
server:
  port: 8080
  name: MyApp

// Java 코드
@Value("${server.port}")
private int port;  // 8080이 자동으로 주입됨!

@Value("${server.name}")
private String name;  // "MyApp"이 자동으로 주입됨!
```

### 🔄 동작 원리

```
1. 애플리케이션 시작
   ↓
2. Spring이 설정 파일 로드
   ↓
3. @Value 애노테이션 발견
   ↓
4. ${...} 표현식 해석
   ↓
5. 해당 값을 필드에 주입
   ↓
6. 빈 생성 완료
```

---
<a id="When-Should-It-Be-Used"></a>
## 2️⃣ 언제 사용해야 할까?

### ✅ Case 1: application.yml/properties 값 주입 (가장 흔함)

#### 설정 파일

```yaml
# application.yml
app:
  admin:
    username: admin
    password: secret1234
  feature:
    email-enabled: true
    max-upload-size: 10485760
```

#### Java 코드

```java
@Component
@RequiredArgsConstructor
public class AppConfig {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.feature.email-enabled}")
    private boolean emailEnabled;

    @Value("${app.feature.max-upload-size}")
    private long maxUploadSize;
}
```

#### ✨ 장점

|         장점         | 설명                        |
| :------------------: | --------------------------- |
| 🚫 **하드코딩 제거** | 코드에 직접 값을 쓰지 않음  |
|  🔄 **환경별 분리**  | dev/prod 설정을 파일로 관리 |
|   🔧 **변경 용이**   | 코드 수정 없이 설정만 변경  |
|  📦 **배포 간소화**  | 환경변수로 오버라이드 가능  |

---

### ✅ Case 2: 환경변수 주입 (보안 정보)

#### 환경변수 설정

```bash
# .env 파일 또는 서버 환경변수
export DB_PASSWORD=mysecretpassword
export JWT_SECRET=jwt-super-secret-key-12345
export AWS_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE
export AWS_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

#### application.yml

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: ${DB_PASSWORD} # 환경변수 참조

jwt:
  secret: ${JWT_SECRET}
  expiration: 3600000

aws:
  credentials:
    access-key: ${AWS_ACCESS_KEY}
    secret-key: ${AWS_SECRET_KEY}
```

#### Java 코드

```java
@Component
public class SecurityConfig {

    @Value("${DB_PASSWORD}")
    private String dbPassword;

    @Value("${JWT_SECRET}")
    private String jwtSecret;

    @Value("${AWS_ACCESS_KEY}")
    private String awsAccessKey;

    @Value("${AWS_SECRET_KEY}")
    private String awsSecretKey;
}
```

#### 🔐 보안 베스트 프랙티스

```
❌ 하지 마세요:
- 비밀번호를 application.yml에 직접 작성
- 소스 코드에 API Key 하드코딩
- Git에 .env 파일 커밋

✅ 하세요:
- 환경변수로 주입
- AWS Secrets Manager / HashiCorp Vault 사용
- .env 파일을 .gitignore에 추가
```

---

### ✅ Case 3: 기본값(Default Value) 설정

#### 기본 문법

```java
@Value("${property.name:defaultValue}")
private String value;
```

#### 실전 예제

```java
@Component
public class ApiConfig {

    // 타임아웃: 설정 없으면 3000ms
    @Value("${app.timeout:3000}")
    private int timeout;

    // 재시도 횟수: 설정 없으면 3번
    @Value("${app.retry.count:3}")
    private int retryCount;

    // 디버그 모드: 설정 없으면 false
    @Value("${app.debug:false}")
    private boolean debugEnabled;

    // API URL: 설정 없으면 기본 URL
    @Value("${external.api.url:https://api.example.com}")
    private String apiUrl;

    // 빈 문자열 기본값
    @Value("${app.description:}")
    private String description;
}
```

#### 💡 활용 시나리오

| 시나리오        | 기본값 활용                      |
| --------------- | -------------------------------- |
| **로컬 개발**   | 기본값으로 빠르게 시작           |
| **선택적 기능** | 기능 활성화 플래그 (기본: false) |
| **호환성**      | 새 설정 추가 시 기존 환경 보호   |
| **Fallback**    | 외부 서비스 URL 대체             |

---

### ✅ Case 4: SpEL(Spring Expression Language) 사용

#### 기본 계산식

```java
@Component
public class CalculationConfig {

    // 단순 계산
    @Value("#{10 * 20}")
    private int result;  // 200

    // 문자열 연결
    @Value("#{'Hello ' + 'World'}")
    private String greeting;  // "Hello World"

    // 조건 연산
    @Value("#{2 > 1 ? 'yes' : 'no'}")
    private String condition;  // "yes"
}
```

#### 시스템 속성 접근

```java
@Component
public class SystemConfig {

    // 사용자 홈 디렉토리
    @Value("#{systemProperties['user.home']}")
    private String userHome;

    // OS 이름
    @Value("#{systemProperties['os.name']}")
    private String osName;

    // Java 버전
    @Value("#{systemProperties['java.version']}")
    private String javaVersion;
}
```

#### 환경변수 접근

```java
@Component
public class EnvironmentConfig {

    @Value("#{systemEnvironment['PATH']}")
    private String path;

    @Value("#{systemEnvironment['HOME']}")
    private String home;
}
```

#### 다른 빈의 속성 참조

```java
@Component
public class AppConfig {
    private String appName = "MyApp";

    public String getAppName() {
        return appName;
    }
}

@Component
public class MessageService {

    // 다른 빈의 메서드 호출
    @Value("#{appConfig.appName}")
    private String appName;

    // 다른 빈의 메서드 호출 + 문자열 연결
    @Value("#{appConfig.appName + ' v1.0'}")
    private String fullName;
}
```

#### ⚠️ SpEL 사용 주의사항

```
✅ 간단한 계산, 시스템 속성 접근
❌ 복잡한 비즈니스 로직
❌ 데이터베이스 쿼리
❌ 외부 API 호출

이유:
- 가독성 저하
- 디버깅 어려움
- 유지보수 복잡
```

---

### ✅ Case 5: 생성자 파라미터 주입 (⭐ 권장!)

#### 기본 패턴

```java
@Component
public class JwtProvider {

    private final String secret;
    private final long expirationTime;

    public JwtProvider(
        @Value("${jwt.secret}") String secret,
        @Value("${jwt.expiration}") long expirationTime
    ) {
        this.secret = secret;
        this.expirationTime = expirationTime;
    }

    public String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
            .signWith(SignatureAlgorithm.HS512, secret)
            .compact();
    }
}
```

#### Lombok 활용

```java
@Component
@RequiredArgsConstructor
public class EmailService {

    @Value("${mail.smtp.host}")
    private final String smtpHost;

    @Value("${mail.smtp.port}")
    private final int smtpPort;

    @Value("${mail.from}")
    private final String fromAddress;

    public void sendEmail(String to, String subject, String content) {
        // 이메일 전송 로직
    }
}
```

> ⚠️ **주의**: Lombok의 `@RequiredArgsConstructor`는 `@Value`와 함께 사용할 수 없습니다. 수동으로 생성자를 작성해야 합니다.

#### 올바른 Lombok 사용

```java
@Component
public class EmailService {

    private final String smtpHost;
    private final int smtpPort;
    private final String fromAddress;

    public EmailService(
        @Value("${mail.smtp.host}") String smtpHost,
        @Value("${mail.smtp.port}") int smtpPort,
        @Value("${mail.from}") String fromAddress
    ) {
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.fromAddress = fromAddress;
    }
}
```

#### ✨ 생성자 주입의 장점

|        장점        | 설명                |
| :----------------: | ------------------- |
|   🔒 **불변성**    | `final` 사용 가능   |
| ✅ **필수값 보장** | 생성 시점에 값 검증 |
| 🧪 **테스트 용이** | Mock 객체 전달 쉬움 |
|   🎯 **명확성**    | 의존성이 명시적     |

---
<a id="Types-of-Values-That-Can-Be-Injected"></a>
## 3️⃣ 주입 가능한 값의 종류

### 📊 값의 출처 우선순위

```
1. JVM 시스템 속성 (-D옵션)
   ↓
2. 환경 변수
   ↓
3. application-{profile}.yml
   ↓
4. application.yml
   ↓
5. 기본값 (:뒤의 값)
```

### 🎯 타입별 주입 예제

#### 1️⃣ 기본 타입 (Primitive & Wrapper)

```java
@Component
public class TypeExamples {

    @Value("${app.count}")
    private int count;

    @Value("${app.count}")
    private Integer countWrapper;

    @Value("${app.price}")
    private double price;

    @Value("${app.enabled}")
    private boolean enabled;

    @Value("${app.code}")
    private char code;

    @Value("${app.amount}")
    private long amount;
}
```

#### 2️⃣ 문자열

```java
@Component
public class StringExamples {

    @Value("${app.name}")
    private String name;

    @Value("${app.description}")
    private String description;

    // 여러 줄 문자열 (YAML에서 |, > 사용)
    @Value("${app.welcome-message}")
    private String welcomeMessage;
}
```

```yaml
# application.yml
app:
  name: MyApp
  description: This is a sample application
  welcome-message: |
    Welcome to MyApp!
    Enjoy your stay.
```

#### 3️⃣ 배열

```java
@Component
public class ArrayExamples {

    @Value("${app.admin-emails}")
    private String[] adminEmails;

    @Value("${app.ports}")
    private int[] ports;
}
```

```yaml
# application.yml
app:
  admin-emails: admin@example.com,support@example.com,info@example.com
  ports: 8080,8081,8082
```

#### 4️⃣ 리스트

```java
@Component
public class ListExamples {

    @Value("${app.allowed-origins}")
    private List<String> allowedOrigins;

    @Value("#{'${app.tags}'.split(',')}")
    private List<String> tags;

    @Value("${app.ids:}")
    private List<Long> ids;
}
```

```yaml
# application.yml
app:
  allowed-origins:
    - http://localhost:3000
    - http://localhost:4200
    - https://example.com
  tags: java,spring,backend
```

#### 5️⃣ 맵

```java
@Component
public class MapExamples {

    // SpEL 사용
    @Value("#{${app.database-config}}")
    private Map<String, String> databaseConfig;

    @Value("#{${app.feature-flags}}")
    private Map<String, Boolean> featureFlags;
}
```

```yaml
# application.yml
app:
  database-config:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    driver: com.mysql.cj.jdbc.Driver
  feature-flags:
    email: true
    sms: false
    push: true
```

#### 6️⃣ 날짜/시간

```java
@Component
public class DateTimeExamples {

    @Value("${app.launch-date}")
    private LocalDate launchDate;

    @Value("${app.last-updated}")
    private LocalDateTime lastUpdated;

    @Value("${app.office-hours-start}")
    private LocalTime officeHoursStart;
}
```

```yaml
# application.yml
app:
  launch-date: 2024-01-15
  last-updated: 2024-01-15T10:30:00
  office-hours-start: 09:00:00
```

---
<a id="Comparison-of-Injection-Methods"></a>
## 4️⃣ 주입 방식 비교

### 📌 3가지 주입 방식

#### 1️⃣ 필드 주입 (Field Injection)

```java
@Component
public class FieldInjection {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String version;
}
```

|    장점     |       단점        |
| :---------: | :---------------: |
| 간결한 코드 | `final` 사용 불가 |
|  빠른 작성  |   테스트 어려움   |
|      -      |    의존성 숨김    |

---

#### 2️⃣ 생성자 주입 (Constructor Injection) ⭐

```java
@Component
public class ConstructorInjection {

    private final String appName;
    private final String version;

    public ConstructorInjection(
        @Value("${app.name}") String appName,
        @Value("${app.version}") String version
    ) {
        this.appName = appName;
        this.version = version;
    }
}
```

|         장점         |        단점        |
| :------------------: | :----------------: |
| `final` 사용 가능 ✅ | 코드가 약간 길어짐 |
|    테스트 용이 ✅    |         -          |
|     불변 객체 ✅     |         -          |
|    의존성 명시 ✅    |         -          |

---

#### 3️⃣ Setter 주입 (Setter Injection)

```java
@Component
public class SetterInjection {

    private String appName;
    private String version;

    @Value("${app.name}")
    public void setAppName(String appName) {
        this.appName = appName;
    }

    @Value("${app.version}")
    public void setVersion(String version) {
        this.version = version;
    }
}
```

|       장점       |       단점        |
| :--------------: | :---------------: |
| 선택적 주입 가능 | `final` 사용 불가 |
|   재주입 가능    | 불변성 보장 안 됨 |

---

### 🏆 권장 순서

```
1순위: 생성자 주입 (Constructor Injection) ⭐⭐⭐
   → 불변성, 테스트 용이성

2순위: 필드 주입 (Field Injection) ⭐⭐
   → 간단한 설정값, 빠른 프로토타이핑

3순위: Setter 주입 (Setter Injection) ⭐
   → 선택적 의존성, 재설정 필요 시
```

---
<a id="Cases-Where-It-Should-Never-Be-Used"></a>
## 5️⃣ 절대 사용하면 안 되는 경우

### ❌ Case 1: 설정이 여러 개인 경우 (묶음 설정)

#### 잘못된 예시

```java
@Component
public class BadExample {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String version;

    @Value("${app.author}")
    private String author;

    @Value("${app.description}")
    private String description;

    @Value("${app.contact.email}")
    private String contactEmail;

    @Value("${app.contact.phone}")
    private String contactPhone;

    @Value("${app.contact.address}")
    private String contactAddress;

    // 설정이 20개, 30개로 늘어나면...? 😱
}
```

#### 문제점

| 문제                    | 설명                            |
| ----------------------- | ------------------------------- |
| 📉 **가독성 저하**      | `@Value`가 클래스 전체에 흩어짐 |
| 🔧 **유지보수 어려움**  | 설정 추가/삭제 시 여러 곳 수정  |
| 🐛 **타입 안정성 부족** | 오타 시 런타임 에러             |
| 📝 **문서화 부족**      | 어떤 설정이 있는지 파악 어려움  |

#### ✅ 올바른 해결책: @ConfigurationProperties

```java
@ConfigurationProperties(prefix = "app")
@Component
@Validated
public class AppProperties {

    private String name;
    private String version;
    private String author;
    private String description;
    private Contact contact;

    @Data
    public static class Contact {
        @Email
        private String email;

        @Pattern(regexp = "\\d{3}-\\d{4}-\\d{4}")
        private String phone;

        private String address;
    }

    // Getters and Setters
}
```

```yaml
# application.yml
app:
  name: MyApp
  version: 1.0.0
  author: John Doe
  description: Sample Application
  contact:
    email: contact@example.com
    phone: 010-1234-5678
    address: Seoul, Korea
```

---

### ❌ Case 2: 비즈니스 로직에 직접 사용

#### 잘못된 예시

```java
@Service
public class DiscountService {

    public boolean isEligibleForDiscount(int price) {
        // 설정값을 직접 비교 ❌
        if (price > @Value("${discount.limit}")) {  // 컴파일 에러!
            return true;
        }
        return false;
    }
}
```

#### 올바른 예시

```java
@Service
public class DiscountService {

    private final int discountLimit;

    public DiscountService(@Value("${discount.limit}") int discountLimit) {
        this.discountLimit = discountLimit;
    }

    public boolean isEligibleForDiscount(int price) {
        return price > discountLimit;
    }
}
```

---

### ❌ Case 3: Static 필드에 주입

#### 작동하지 않는 예시

```java
@Component
public class StaticFieldExample {

    @Value("${app.name}")
    private static String appName;  // ❌ null이 됨!

    public static void printAppName() {
        System.out.println(appName);  // null 출력!
    }
}
```

#### 이유

```
Spring은 인스턴스 레벨에서 의존성을 주입합니다.
Static 필드는 클래스 레벨이므로 주입이 불가능합니다.
```

#### 해결 방법 1: Setter 사용

```java
@Component
public class StaticFieldSolution {

    private static String appName;

    @Value("${app.name}")
    public void setAppName(String name) {
        StaticFieldSolution.appName = name;
    }

    public static String getAppName() {
        return appName;
    }
}
```

#### 해결 방법 2: 인스턴스 필드로 변경 (권장)

```java
@Component
public class InstanceFieldSolution {

    private final String appName;

    public InstanceFieldSolution(@Value("${app.name}") String appName) {
        this.appName = appName;
    }

    public String getAppName() {
        return appName;
    }
}
```

---

### ❌ Case 4: Interface나 Abstract Class에 사용

#### 작동하지 않는 예시

```java
public interface ConfigInterface {

    @Value("${app.name}")  // ❌ 작동 안 함!
    String APP_NAME = "";
}
```

```java
public abstract class ConfigAbstract {

    @Value("${app.name}")  // ❌ 작동 안 함!
    protected String appName;
}
```

#### 해결책

구현 클래스에서 주입받기

```java
@Component
public class ConfigImpl implements ConfigInterface {

    @Value("${app.name}")
    private String appName;
}
```

---
<a id="value-vs-configurationproperties"></a>
## 6️⃣ @Value vs @ConfigurationProperties

### 📊 상세 비교

|      항목       |       @Value       | @ConfigurationProperties |
| :-------------: | :----------------: | :----------------------: |
|  **사용 대상**  |      단일 값       |      여러 설정 묶음      |
| **타입 안정성** | 낮음 (런타임 체크) |    높음 (컴파일 체크)    |
|    **검증**     |        수동        |  `@Validated` + JSR-303  |
|  **IDE 지원**   |       제한적       |       자동완성 ✅        |
|   **문서화**    |        부족        |     메타데이터 생성      |
|  **재사용성**   |        낮음        |           높음           |
| **복잡한 구조** |       어려움       |           쉬움           |
|  **SpEL 지원**  |         ✅         |            ❌            |

---

### 🎯 언제 무엇을 사용할까?

#### @Value를 사용하는 경우

```java
@Component
public class SingleValueConfig {

    // ✅ 단일 값 주입
    @Value("${jwt.secret}")
    private String jwtSecret;

    // ✅ 간단한 기본값
    @Value("${app.timeout:3000}")
    private int timeout;

    // ✅ SpEL 표현식
    @Value("#{systemProperties['user.home']}")
    private String userHome;
}
```

**사용 시나리오:**

- 1-3개의 간단한 설정
- 다른 빈의 속성 참조 필요
- SpEL 표현식 사용 필요

---

#### @ConfigurationProperties를 사용하는 경우

```java
@ConfigurationProperties(prefix = "app")
@Component
@Validated
public class AppConfig {

    @NotBlank
    private String name;

    @Min(1)
    @Max(65535)
    private int port;

    @Email
    private String adminEmail;

    private List<String> allowedOrigins;

    private Map<String, String> metadata;

    private Database database;

    @Data
    @Validated
    public static class Database {
        @NotBlank
        private String url;

        @NotBlank
        private String username;

        private String password;

        @Min(1)
        private int maxConnections = 10;
    }

    // Getters and Setters
}
```

```yaml
# application.yml
app:
  name: MyApp
  port: 8080
  admin-email: admin@example.com
  allowed-origins:
    - http://localhost:3000
    - https://example.com
  metadata:
    version: 1.0.0
    author: John Doe
  database:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
    max-connections: 20
```

**사용 시나리오:**

- 4개 이상의 관련 설정
- 계층적 구조 (nested objects)
- 컬렉션 (List, Map)
- 입력 검증 필요
- 타입 안전성 중요

---

### 🔄 마이그레이션 예시

#### Before: @Value (나쁜 예)

```java
@Component
public class EmailConfig {
    @Value("${mail.smtp.host}")
    private String host;

    @Value("${mail.smtp.port}")
    private int port;

    @Value("${mail.smtp.username}")
    private String username;

    @Value("${mail.smtp.password}")
    private String password;

    @Value("${mail.smtp.auth}")
    private boolean auth;

    @Value("${mail.smtp.starttls.enable}")
    private boolean starttlsEnable;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.templates.welcome}")
    private String welcomeTemplate;

    @Value("${mail.templates.reset-password}")
    private String resetPasswordTemplate;
}
```

#### After: @ConfigurationProperties (좋은 예)

```java
@ConfigurationProperties(prefix = "mail")
@Component
@Validated
public class EmailProperties {

    private Smtp smtp;
    private String from;
    private Templates templates;

    @Data
    @Validated
    public static class Smtp {
        @NotBlank
        private String host;

        @Min(1)
        @Max(65535)
        private int port;

        private String username;
        private String password;
        private boolean auth = true;
        private boolean starttlsEnable = true;
    }

    @Data
    public static class Templates {
        private String welcome;
        private String resetPassword;
    }

    // Getters and Setters
}
```

---
<a id="In-thefield-pattern-and-best-practices"></a>
## 7️⃣ 실무 패턴과 Best Practices

### 🌟 패턴 1: 환경별 설정 분리

#### 파일 구조

```
src/main/resources/
├── application.yml              # 공통 설정
├── application-dev.yml          # 개발 환경
├── application-staging.yml      # 스테이징 환경
└── application-prod.yml         # 운영 환경
```

#### application.yml (공통)

```yaml
spring:
  application:
    name: myapp

app:
  name: MyApp
  version: 1.0.0
```

#### application-dev.yml

```yaml
app:
  debug: true
  log-level: DEBUG

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb_dev
```

#### application-prod.yml

```yaml
app:
  debug: false
  log-level: INFO

spring:
  datasource:
    url: jdbc:mysql://prod-server:3306/mydb_prod
```

#### 활성화

```bash
# 개발 환경
java -jar app.jar --spring.profiles.active=dev

# 운영 환경
java -jar app.jar --spring.profiles.active=prod
```

---

### 🌟 패턴 2: 보안 정보 관리

#### application.yml

```yaml
app:
  jwt:
    secret: ${JWT_SECRET}
    expiration: ${JWT_EXPIRATION:3600000}

spring:
  datasource:
    url: jdbc:mysql://${DB_HOST:localhost}:${DB_PORT:3306}/${DB_NAME}
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

aws:
  credentials:
    access-key: ${AWS_ACCESS_KEY}
    secret-key: ${AWS_SECRET_KEY}
  region: ${AWS_REGION:ap-northeast-2}
```

#### Docker Compose

```yaml
version: "3.8"
services:
  app:
    image: myapp:latest
    environment:
      - JWT_SECRET=your-jwt-secret-key
      - JWT_EXPIRATION=3600000
      - DB_HOST=mysql
      - DB_PORT=3306
      - DB_NAME=mydb
      - DB_USERNAME=root
      - DB_PASSWORD=secret
      - AWS_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE
      - AWS_SECRET_KEY=wJalrXUtnFEMI/K7MDENG
      - AWS_REGION=ap-northeast-2
```

---

### 🌟 패턴 3: 검증과 예외 처리

```java
@Component
public class ValidatedConfig {

    private final String jwtSecret;
    private final long jwtExpiration;

    public ValidatedConfig(
        @Value("${jwt.secret}") String jwtSecret,
        @Value("${jwt.expiration}") long jwtExpiration
    ) {
        // 검증 로직
        if (jwtSecret == null || jwtSecret.length() < 32) {
            throw new IllegalArgumentException(
                "JWT secret must be at least 32 characters"
            );
        }

        if (jwtExpiration <= 0) {
            throw new IllegalArgumentException(
                "JWT expiration must be positive"
            );
        }

        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }
}
```

---

### 🌟 패턴 4: 타입 변환 활용

#### 커스텀 타입 변환

```java
// Enum
public enum Environment {
    DEV, STAGING, PROD
}

@Component
public class EnvironmentConfig {

    @Value("${app.environment}")
    private Environment environment;  // 자동 변환!
}
```

```yaml
app:
  environment: PROD
```

#### Duration / DataSize

```java
@Component
public class TimeConfig {

    @Value("${app.timeout}")
    private Duration timeout;  // PT30S (30초)

    @Value("${app.max-size}")
    private DataSize maxSize;  // 10MB
}
```

```yaml
app:
  timeout: 30s
  max-size: 10MB
```

---

### 🌟 패턴 5: Profile별 Bean 생성

```java
@Configuration
public class DataSourceConfig {

    @Bean
    @Profile("dev")
    public DataSource devDataSource(
        @Value("${spring.datasource.url}") String url
    ) {
        return DataSourceBuilder.create()
            .url(url)
            .username("dev_user")
            .password("dev_password")
            .build();
    }

    @Bean
    @Profile("prod")
    public DataSource prodDataSource(
        @Value("${spring.datasource.url}") String url,
        @Value("${spring.datasource.username}") String username,
        @Value("${spring.datasource.password}") String password
    ) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(20);
        config.setConnectionTimeout(30000);

        return new HikariDataSource(config);
    }
}
```

---
<a id="common-mistake-and-solution"></a>
## 8️⃣ 흔한 실수와 해결책

### ⚠️ 실수 1: 존재하지 않는 프로퍼티

#### 문제 코드

```java
@Component
public class BadConfig {

    @Value("${not.exist.property}")  // 이 키가 없음!
    private String value;
}
```

#### 에러 메시지

```
Caused by: java.lang.IllegalArgumentException:
Could not resolve placeholder 'not.exist.property' in value "${not.exist.property}"
```

#### 해결책 1: 기본값 설정

```java
@Value("${not.exist.property:default-value}")
private String value;
```

#### 해결책 2: Optional 사용

```java
@Value("${optional.property:#{null}}")
private String optionalValue;

// 또는
@Value("${optional.property:}")
private String optionalValue;  // 빈 문자열
```

---

### ⚠️ 실수 2: 타입 불일치

#### 문제 코드

```java
@Value("${app.port}")
private int port;
```

```yaml
app:
  port: "8080" # 문자열로 설정
```

#### 에러 메시지

```
Failed to convert value of type 'java.lang.String' to required type 'int'
```

#### 해결책

```yaml
app:
  port: 8080 # 문자열 따옴표 제거
```

또는

```java
@Value("${app.port}")
private String port;  // 타입을 String으로 변경

private int getPortAsInt() {
    return Integer.parseInt(port);
}
```

---

### ⚠️ 실수 3: 순환 참조

#### 문제 코드

```yaml
app:
  name: ${app.full-name}
  full-name: ${app.name} v1.0
```

#### 에러 메시지

```
Circular placeholder reference 'app.name' in property definitions
```

#### 해결책

```yaml
app:
  name: MyApp
  full-name: ${app.name} v1.0 # 순환 제거
```

---

### ⚠️ 실수 4: 프로파일 오타

#### 문제 코드

```java
@Component
@Profile("proudction")  // 오타: production
public class ProdConfig {
    // 절대 로드되지 않음!
}
```

#### 해결책

```java
@Component
@Profile("production")  // 올바른 프로파일명
public class ProdConfig {
}
```

**팁**: 프로파일명을 상수로 관리

```java
public class Profiles {
    public static final String DEV = "dev";
    public static final String STAGING = "staging";
    public static final String PROD = "production";
}

@Profile(Profiles.PROD)
public class ProdConfig {
}
```

---

### ⚠️ 실수 5: List/Map 주입 실패

#### 문제 코드

```java
@Value("${app.emails}")
private List<String> emails;  // 작동 안 함!
```

```yaml
app:
  emails: admin@example.com,user@example.com
```

#### 해결책

```java
// 방법 1: SpEL 사용
@Value("#{'${app.emails}'.split(',')}")
private List<String> emails;

// 방법 2: 배열로 받기
@Value("${app.emails}")
private String[] emails;

// 방법 3: @ConfigurationProperties 사용 (권장)
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private List<String> emails;
}
```

```yaml
app:
  emails:
    - admin@example.com
    - user@example.com
```

---
<a id="test-strategy"></a>
## 9️⃣ 테스트 전략

### 🧪 단위 테스트

#### 생성자 주입 테스트

```java
@ExtendWith(MockitoExtension.class)
class JwtProviderTest {

    private JwtProvider jwtProvider;

    @BeforeEach
    void setUp() {
        // Mock 없이 직접 값 주입
        jwtProvider = new JwtProvider(
            "test-secret-key-at-least-32-chars",
            3600000L
        );
    }

    @Test
    void generateToken_Success() {
        String token = jwtProvider.generateToken("testuser");
        assertThat(token).isNotNull();
    }
}
```

---

### 🧪 통합 테스트

#### @TestPropertySource 사용

```java
@SpringBootTest
@TestPropertySource(properties = {
    "jwt.secret=test-secret-key",
    "jwt.expiration=3600000",
    "app.name=TestApp"
})
class IntegrationTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Test
    void contextLoads() {
        assertThat(jwtProvider).isNotNull();
    }
}
```

---

#### application-test.yml 사용

```yaml
# src/test/resources/application-test.yml
jwt:
  secret: test-secret-key-for-testing
  expiration: 3600000

spring:
  datasource:
    url: jdbc:h2:mem:testdb
    username: sa
    password:
```

```java
@SpringBootTest
@ActiveProfiles("test")
class IntegrationTest {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Test
    void testJwtSecret() {
        assertThat(jwtSecret).isEqualTo("test-secret-key-for-testing");
    }
}
```

---

#### @DynamicPropertySource 사용 (동적 프로퍼티)

```java
@SpringBootTest
@Testcontainers
class DatabaseIntegrationTest {

    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql::getJdbcUrl);
        registry.add("spring.datasource.username", mysql::getUsername);
        registry.add("spring.datasource.password", mysql::getPassword);
    }

    @Test
    void testDatabaseConnection() {
        // 테스트 로직
    }
}
```

---
<a id="core-summary"></a>
## 🔟 핵심 요약

### 💡 한 문장 정리

> **@Value는 외부 설정을 코드에 주입하는 Spring의 핵심 애노테이션으로, 단일 값이나 간단한 설정에 적합하다.**

---

### 📌 기억해야 할 핵심

|      항목       | 내용                                 |
| :-------------: | ------------------------------------ |
|  **주요 용도**  | 설정 파일, 환경변수 주입             |
|  **권장 주입**  | 생성자 주입 (불변성)                 |
|   **기본값**    | `:` 사용 (`${key:default}`)          |
| **복잡한 설정** | `@ConfigurationProperties` 사용      |
|  **주의사항**   | Static 필드 불가, 프로퍼티 존재 확인 |

---

### 🎓 실무 체크리스트

#### 기본 사용

- [ ] 생성자 주입 방식 사용
- [ ] 기본값 설정으로 안전성 확보
- [ ] 환경별 설정 파일 분리 (dev/prod)
- [ ] 보안 정보는 환경변수로 관리

#### 고급 사용

- [ ] 4개 이상 설정은 `@ConfigurationProperties`로 전환
- [ ] `@Validated`로 입력 검증 추가
- [ ] Profile별 Bean 생성 활용
- [ ] 타입 변환 (Duration, DataSize) 활용

#### 주의사항

- [ ] Static 필드에 사용하지 않음
- [ ] 존재하지 않는 프로퍼티 확인
- [ ] 타입 불일치 주의
- [ ] 순환 참조 방지
- [ ] SpEL 복잡도 제한

---

### 📊 상황별 사용 가이드

```
1-3개 간단한 설정 → @Value
4개 이상 관련 설정 → @ConfigurationProperties
보안 정보 → 환경변수 + @Value
계층 구조 → @ConfigurationProperties
입력 검증 필요 → @ConfigurationProperties + @Validated
SpEL 표현식 → @Value
```

---

## 🚀 다음 학습 주제

- Spring Cloud Config - 중앙화된 설정 관리
- Vault Integration - 보안 정보 관리
- PropertySource 우선순위와 오버라이딩
- Spring Boot Actuator - 런타임 설정 확인
- Externalized Configuration 심화

---

## 💬 FAQ

<details>
<summary><strong>Q1. @Value와 Environment의 차이는?</strong></summary>

**@Value**:

- 필드/생성자에 직접 주입
- 컴파일 타임에 타입 체크
- SpEL 지원

```java
@Value("${app.name}")
private String appName;
```

**Environment**:

- 프로그래매틱하게 접근
- 런타임에 조건부 조회 가능
- 더 유연한 제어

```java
@Autowired
private Environment env;

public void printAppName() {
    String appName = env.getProperty("app.name", "default");
    System.out.println(appName);
}
```

**선택 기준:**

- 고정된 설정 → `@Value`
- 동적 조회 필요 → `Environment`
</details>

<details>
<summary><strong>Q2. application.yml과 application.properties의 차이는?</strong></summary>

**application.yml**:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: secret
```

**장점**: 계층 구조 표현이 명확, 중복 감소

**application.properties**:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mydb
spring.datasource.username=root
spring.datasource.password=secret
```

**장점**: 간단, 전통적

**권장**: YAML (더 읽기 쉽고 유지보수 편함)

</details>

<details>
<summary><strong>Q3. @Value로 주입한 값을 런타임에 변경할 수 있나요?</strong></summary>

**기본적으로 불가능합니다.**

`@Value`는 Bean 생성 시점에 한 번만 주입됩니다.

**대안:**

1. Spring Cloud Config + `@RefreshScope`
2. `Environment.getProperty()`로 매번 조회
3. `@ConfigurationProperties` + Actuator Refresh

```java
@Component
@RefreshScope  // Spring Cloud Config 필요
public class RefreshableConfig {

    @Value("${dynamic.property}")
    private String dynamicProperty;
}
```

</details>

<details>
<summary><strong>Q4. 프로퍼티 파일의 우선순위는?</strong></summary>

**높은 우선순위 → 낮은 우선순위**:

1. 명령줄 인자 (`--server.port=9090`)
2. JVM 시스템 속성 (`-Dserver.port=9090`)
3. OS 환경변수 (`SERVER_PORT=9090`)
4. `application-{profile}.yml`
5. `application.yml`
6. `@PropertySource`로 지정한 파일
7. 기본값 (`:` 뒤의 값)

**예시:**

```bash
# application.yml: port=8080
# 환경변수: SERVER_PORT=9090
# 실제 사용 값: 9090 (환경변수가 우선)
```

</details>

<details>
<summary><strong>Q5. SpEL에서 null 체크하는 방법은?</strong></summary>

```java
// Elvis 연산자 사용
@Value("#{config.value ?: 'default'}")
private String value;

// Safe Navigation 연산자
@Value("#{config?.value}")
private String value;

// 조건 연산자
@Value("#{config.value != null ? config.value : 'default'}")
private String value;
```

</details>

---

## 📚 참고 자료

### 공식 문서

- [Spring Framework - PropertySource](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#beans-property-source-abstraction)
- [Spring Boot - Externalized Configuration](https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config)
- [Spring Expression Language (SpEL)](https://docs.spring.io/spring-framework/docs/current/reference/html/core.html#expressions)

### 추천 강의

- 인프런: "스프링 핵심 원리 - 기본편" (김영한)
- Udemy: "Spring Framework 6: Beginner to Guru"

### 추천 도서

- "스프링 부트 실전 활용 마스터" - 그렉 턴퀴스트
- "Spring in Action" - Craig Walls

---

**🎉 이제 @Value를 완벽하게 이해하고 실무에 적용할 수 있습니다!**

> 💡 **마지막 조언**: 간단한 설정은 `@Value`, 복잡한 설정은 `@ConfigurationProperties`를 사용하세요. 보안 정보는 반드시 환경변수로 관리하세요!


