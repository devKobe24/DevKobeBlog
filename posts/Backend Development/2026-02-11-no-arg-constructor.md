---
title: 📦 @NoArgsConstructor 완벽 가이드
published: true
tags:
    - JPA
    - Spring Data JPA
    - Hibernate
    - BaseEntity
    - ORM
    - Backend
    - Java
    - Lombok

date: 2026-02-08
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# @NoArgsConstructor 완벽 가이드

> 💡 **핵심**: JPA Entity에서 거의 필수인 Lombok 어노테이션  
> 기본 생성자를 자동 생성하여 리플렉션 기반 프레임워크 요구사항을 충족한다.

---

## 🎯 @NoArgsConstructor란?

### 정의

**파라미터가 없는 기본 생성자(Default Constructor)를 자동 생성하는 Lombok 어노테이션**

```java
// Lombok 사용 전
public class Member {
    private String name;

    public Member() {  // 직접 작성해야 함
    }
}

// Lombok 사용 후
@NoArgsConstructor
public class Member {
    private String name;
    // 컴파일 시 public Member() {} 자동 생성
}
```

---

## 🔍 기본 생성자의 이해

### 자동 생성 규칙

```java
// Case 1: 생성자가 하나도 없으면 자동 생성 ✅
public class Member {
    private String name;
    // 컴파일러가 자동으로 public Member() {} 생성
}

// Case 2: 생성자가 하나라도 있으면 자동 생성 안 됨 ❌
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
    // 기본 생성자가 자동 생성되지 않음!
}
```

### 문제 상황

```java
@Entity
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
}

// JPA 사용 시
Member member = entityManager.find(Member.class, 1L);
// ❌ org.hibernate.InstantiationException:
//    No default constructor for entity: Member
```

### 해결책

```java
@Entity
@NoArgsConstructor  // ✅ 기본 생성자 자동 생성
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
}
```

---

## 🚀 언제 사용하나?

### 1. JPA Entity (거의 필수) ⭐⭐⭐

**왜 필요한가?**

- JPA/Hibernate는 **리플렉션(Reflection)** 을 사용하여 객체 생성
- 데이터베이스에서 조회한 결과를 Entity 객체로 매핑할 때 기본 생성자 사용

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}
```

### 2. Jackson 역직렬화

**JSON → 객체 변환 시 기본 생성자 필요**

```java
@NoArgsConstructor
public class MemberRequest {
    private String name;
    private String email;

    // Jackson이 JSON을 이 객체로 변환할 때 기본 생성자 사용
}
```

### 3. 테스트용 POJO

```java
@NoArgsConstructor
public class TestData {
    private String value;

    public void setValue(String value) {
        this.value = value;
    }
}

// 테스트에서
TestData data = new TestData();
data.setValue("test");
```

---

## 🎨 실무 패턴: access = AccessLevel.PROTECTED

### 가장 많이 보는 형태

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    private Long id;
    private String name;

    // 정적 팩토리 메서드로 객체 생성 유도
    public static Member of(String name) {
        Member member = new Member();
        member.name = name;
        return member;
    }
}
```

### 왜 PROTECTED를 사용하나?

| 접근 제어자   | JPA 사용 가능 | 외부 생성 차단 | 추천도     |
| ------------- | ------------- | -------------- | ---------- |
| **PUBLIC**    | ✅            | ❌             | ⭐⭐       |
| **PROTECTED** | ✅            | ✅             | ⭐⭐⭐⭐⭐ |
| **PRIVATE**   | ❌            | ✅             | ❌         |

**PROTECTED의 장점**:

```java
// 같은 패키지나 상속 관계에서만 접근 가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    // ...
}

// 외부에서 직접 생성 불가 ✅
// Member member = new Member(); // 컴파일 에러

// JPA는 프록시나 리플렉션으로 접근 가능 ✅
Member member = entityManager.find(Member.class, 1L); // OK
```

**핵심 이유**:

1. **캡슐화 유지**: 외부에서 `new Member()` 직접 생성 방지
2. **객체 생성 통제**: 팩토리 메서드나 Builder로 유도
3. **JPA 요구사항 충족**: JPA는 여전히 접근 가능

---

## 🔥 실무 필수 조합

### 패턴 1: Entity 표준 구성

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Builder
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }
}

// 사용
Member member = Member.builder()
    .name("홍길동")
    .email("hong@example.com")
    .build();
```

### 패턴 2: Entity + AllArgsConstructor

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
}
```

**주의**: `@AllArgsConstructor`는 필드 순서 변경 시 버그 위험

### 패턴 3: DTO 구성

```java
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MemberResponse {
    private Long id;
    private String name;
    private String email;

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getEmail()
        );
    }
}
```

---

## ⚠️ 주의사항과 해결책

### 1. final 필드 문제

```java
// ❌ 컴파일 에러 발생
@NoArgsConstructor
public class Member {
    private final String name;  // final 필드는 초기화 필수
}

// 에러 메시지:
// variable name might not have been initialized
```

**해결 방법**:

#### Option 1: force = true (비추천)

```java
@NoArgsConstructor(force = true)
public class Member {
    private final String name;  // null로 초기화됨
    private final int age;      // 0으로 초기화됨
}

// ⚠️ 위험: final의 의미가 퇴색됨
```

#### Option 2: final 제거 (추천)

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private String name;  // final 제거

    @Builder
    public Member(String name) {
        this.name = name;
    }
}
```

#### Option 3: @RequiredArgsConstructor 사용

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Member {
    private final String name;

    // @RequiredArgsConstructor가 final 필드용 생성자 생성
}
```

### 2. 상속 관계에서의 주의사항

```java
// 부모 클래스
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BaseEntity {
    private LocalDateTime createdAt;
}

// 자식 클래스
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseEntity {
    @Id
    private Long id;
    private String name;
}

// ✅ 정상 작동
```

### 3. Jackson 역직렬화 시 접근 제어자

```java
// ❌ Jackson은 PROTECTED 생성자 사용 불가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {
    private String name;
}

// ✅ PUBLIC으로 설정해야 함
@NoArgsConstructor  // 기본값이 PUBLIC
public class MemberRequest {
    private String name;
}
```

---

## 📊 사용 케이스별 가이드

| 상황               | 사용 여부 | 접근 제어자 | 비고                                |
| ------------------ | --------- | ----------- | ----------------------------------- |
| **JPA Entity**     | ✅ 필수   | `PROTECTED` | 표준 패턴                           |
| **Request DTO**    | ✅ 권장   | `PUBLIC`    | Jackson 역직렬화                    |
| **Response DTO**   | △ 선택    | `PUBLIC`    | 정적 팩토리 메서드로 생성 시 불필요 |
| **도메인 모델**    | ❌ 비권장 | -           | 생성자로 불변성 보장                |
| **테스트 Fixture** | ✅ 유용   | `PUBLIC`    | 테스트 편의성                       |

---

## 🎯 Best Practice 체크리스트

### JPA Entity 작성 시

- [ ] `@NoArgsConstructor(access = AccessLevel.PROTECTED)` 사용
- [ ] Builder 패턴이나 정적 팩토리 메서드로 객체 생성
- [ ] final 필드는 신중하게 사용 (force = true 지양)
- [ ] 기본 생성자로 직접 생성하지 않도록 설계

### DTO 작성 시

- [ ] Jackson 사용 시 PUBLIC 생성자 필요
- [ ] 불변 객체가 필요하면 `@AllArgsConstructor` 고려
- [ ] 빌더 패턴 필요성 검토

### 실무 코드 예시

```java
// ✅ 추천하는 Entity 패턴
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Builder
    public Member(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // 정적 팩토리 메서드 (선택)
    public static Member of(String name, String email) {
        return Member.builder()
            .name(name)
            .email(email)
            .build();
    }

    // 비즈니스 로직
    public void updateEmail(String email) {
        this.email = email;
    }
}
```

```java
// ✅ 추천하는 Request DTO 패턴
@Getter
@NoArgsConstructor  // Jackson 역직렬화용
public class MemberCreateRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    // 테스트나 빌더 필요 시
    @Builder
    public MemberCreateRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Entity 변환 메서드
    public Member toEntity() {
        return Member.builder()
            .name(this.name)
            .email(this.email)
            .build();
    }
}
```

```java
// ✅ 추천하는 Response DTO 패턴
@Getter
@AllArgsConstructor  // 정적 팩토리 메서드용
public class MemberResponse {

    private Long id;
    private String name;
    private String email;

    public static MemberResponse from(Member member) {
        return new MemberResponse(
            member.getId(),
            member.getName(),
            member.getEmail()
        );
    }
}
```

---

## 🔬 내부 동작 원리

### 컴파일 결과 비교

**Before (Lombok 사용 전)**:

```java
public class Member {
    private String name;

    public Member() {
    }

    public Member(String name) {
        this.name = name;
    }
}
```

**After (Lombok 사용 후)**:

```java
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
}

// ↓ 컴파일 후 ↓

public class Member {
    private String name;

    protected Member() {  // Lombok이 생성
    }

    public Member(String name) {
        this.name = name;
    }
}
```

### JPA가 사용하는 방식

```java
// JPA/Hibernate 내부 동작 (단순화)
public <T> T createEntity(Class<T> clazz) {
    try {
        // 1. 기본 생성자 찾기
        Constructor<T> constructor = clazz.getDeclaredConstructor();

        // 2. 접근 가능하게 설정 (PROTECTED도 가능)
        constructor.setAccessible(true);

        // 3. 객체 생성
        T instance = constructor.newInstance();

        // 4. 필드 값 주입 (Reflection)
        Field nameField = clazz.getDeclaredField("name");
        nameField.setAccessible(true);
        nameField.set(instance, "홍길동");

        return instance;
    } catch (Exception e) {
        throw new RuntimeException("Entity 생성 실패", e);
    }
}
```

---

## 📋 핵심 요약

### @NoArgsConstructor의 3가지 핵심

1. **자동 생성**: 파라미터 없는 기본 생성자 자동 생성
2. **리플렉션 대응**: JPA, Jackson 등 리플렉션 기반 프레임워크 요구사항 충족
3. **캡슐화 유지**: `access = PROTECTED`로 외부 생성 차단하면서 프레임워크는 사용 가능

### 실무 패턴 요약

```java
// JPA Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)

// Request DTO
@NoArgsConstructor  // PUBLIC (기본값)

// Response DTO
// 정적 팩토리 메서드 사용 시 불필요
```

### 주의사항 3가지

1. **final 필드와 충돌** → final 제거 또는 force = true (비추천)
2. **Jackson은 PROTECTED 불가** → PUBLIC 사용
3. **외부 생성 차단** → PROTECTED + Builder/Factory 조합

---

## 💭 자주 묻는 질문 (FAQ)

### Q1. @NoArgsConstructor vs @AllArgsConstructor?

```java
// @NoArgsConstructor: 파라미터 없는 생성자
@NoArgsConstructor
public class Member {
    private String name;
    // public Member() {}
}

// @AllArgsConstructor: 모든 필드를 파라미터로 받는 생성자
@AllArgsConstructor
public class Member {
    private String name;
    private String email;
    // public Member(String name, String email) { ... }
}

// 둘 다 사용 가능
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Member {
    // protected Member() {}
    // public Member(String name, String email) {}
}
```

### Q2. 왜 JPA는 기본 생성자가 필수인가?

**리플렉션을 사용하기 때문**:

- JPA는 데이터베이스 조회 결과를 객체로 변환
- 먼저 기본 생성자로 빈 객체 생성
- 그 다음 각 필드에 값 주입 (Reflection)

### Q3. Builder만 있으면 되지 않나?

```java
// Builder만 있는 경우
@Builder
public class Member {
    private String name;
    // ❌ JPA 사용 시 에러 발생
}

// 해결: NoArgsConstructor 추가
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class Member {
    private String name;
    // ✅ JPA와 Builder 모두 사용 가능
}
```

### Q4. force = true는 언제 사용하나?

**거의 사용하지 않음** (안티 패턴에 가까움)

```java
// final의 의미가 없어짐
@NoArgsConstructor(force = true)
public class Member {
    private final String name;  // null로 초기화
    // final인데 null? 🤔
}

// 차라리 final 제거하는 게 나음
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private String name;  // 명확함
}
```

---

## 🎁 보너스: 실전 트러블슈팅

### 문제 1: InstantiationException

```java
// 문제 코드
@Entity
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
}

// 에러
// org.hibernate.InstantiationException:
// No default constructor for entity: Member
```

**해결**:

```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // ✅
public class Member {
    private String name;

    public Member(String name) {
        this.name = name;
    }
}
```

### 문제 2: Jackson 역직렬화 실패

```java
// 문제 코드
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberRequest {
    private String name;
}

// JSON → 객체 변환 시 에러
```

**해결**:

```java
@NoArgsConstructor  // PUBLIC으로 변경 ✅
public class MemberRequest {
    private String name;
}
```

### 문제 3: final 필드 컴파일 에러

```java
// 문제 코드
@NoArgsConstructor
public class Member {
    private final Long id;  // ❌ 컴파일 에러
}
```

**해결**:

```java
// Option 1: final 제거 (추천)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {
    private Long id;  // ✅
}

// Option 2: @RequiredArgsConstructor 함께 사용
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@RequiredArgsConstructor
public class Member {
    private final Long id;  // ✅
}
```

---

## 🎯 마지막 체크포인트

**다음 질문에 답할 수 있다면 이해 완료**:

- [ ] @NoArgsConstructor가 하는 일을 설명할 수 있는가?
- [ ] JPA Entity에 왜 필요한지 설명할 수 있는가?
- [ ] PROTECTED를 사용하는 이유를 설명할 수 있는가?
- [ ] final 필드와의 충돌을 해결할 수 있는가?
- [ ] Entity, Request DTO, Response DTO에 각각 어떻게 적용해야 하는가?

**Remember**:

```
@NoArgsConstructor = JPA의 필수 요구사항
access = PROTECTED = 실무 표준 패턴
캡슐화 + 프레임워크 호환 = 최적의 조합
```
