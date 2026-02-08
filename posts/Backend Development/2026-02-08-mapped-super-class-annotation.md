---
title: 📦 @MappedSuperclass 완벽 가이드
published: true
tags:
    - JPA
    - Spring Data JPA
    - Hibernate
    - BaseEntity
    - ORM
    - Backend
    - Java

date: 2026-02-08
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# 📦 @MappedSuperclass 완벽 가이드

> JPA에서 공통 필드를 우아하게 관리하는 방법

## 💡 @MappedSuperclass란?

**정의**: 엔티티들이 공통으로 사용하는 필드를 모아둔 **상속 전용 부모 클래스**

### 핵심 3원칙

```
1️⃣ DB 테이블로 매핑되지 않음 ❌
2️⃣ 자식 엔티티가 필드를 상속받아 자신의 컬럼으로 사용 ✅
3️⃣ 직접 조회 불가능 (JPQL/EntityManager) ❌
```

---

## 🎯 기본 예제

### 부모 클래스 (BaseEntity)

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 자식 엔티티 (Member)

```java
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;
}
```

### 생성되는 테이블

```sql
-- ✅ members 테이블만 생성됨
CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- ❌ base_entity 테이블은 생성되지 않음!
```

---

## 🔍 @MappedSuperclass vs @Entity 상속

### 비교 테이블

| 특성            | @MappedSuperclass   | @Entity + @Inheritance |
| --------------- | ------------------- | ---------------------- |
| **테이블 생성** | ❌ 부모 테이블 없음 | ✅ 전략에 따라 생성    |
| **다형성 조회** | ❌ 불가능           | ✅ 가능                |
| **JPQL 조회**   | ❌ 불가능           | ✅ 가능                |
| **용도**        | 공통 필드 상속      | 도메인 모델 상속       |
| **관계 매핑**   | ❌ 불가능           | ✅ 가능                |

### 코드 비교

#### @MappedSuperclass 방식

```java
@MappedSuperclass
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Entity
public class Product extends BaseEntity {
    @Id private Long id;
    private String name;
}

// ❌ 불가능
List<BaseEntity> all = em.createQuery(
    "SELECT b FROM BaseEntity b",
    BaseEntity.class
).getResultList();

// ✅ 가능
List<Product> products = em.createQuery(
    "SELECT p FROM Product p",
    Product.class
).getResultList();
```

#### @Entity + @Inheritance 방식

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "dtype")
public abstract class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
}

@Entity
@DiscriminatorValue("BOOK")
public class Book extends Item {
    private String author;
    private String isbn;
}

// ✅ 다형성 조회 가능!
List<Item> items = em.createQuery(
    "SELECT i FROM Item i",
    Item.class
).getResultList();
// Book, Album, Movie 모두 조회됨
```

---

## 📝 실무 사용 패턴

### 패턴 1️⃣: BaseTimeEntity (시간 추적)

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

**사용:**

```java
@Entity
public class Post extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long id;
    private String title;
    private String content;
    // createdAt, updatedAt 자동 포함!
}

@Entity
public class Comment extends BaseTimeEntity {
    @Id @GeneratedValue
    private Long id;
    private String content;
    // createdAt, updatedAt 자동 포함!
}
```

### 패턴 2️⃣: BaseEntity (생성자/수정자 추적)

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;
}
```

**Spring Security와 연계:**

```java
@Configuration
@EnableJpaAuditing
public class JpaConfig {

    @Bean
    public AuditorAware<String> auditorProvider() {
        return () -> {
            Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

            if (authentication == null || !authentication.isAuthenticated()) {
                return Optional.of("SYSTEM");
            }

            return Optional.of(authentication.getName());
        };
    }
}
```

### 패턴 3️⃣: 소프트 삭제 (Soft Delete)

```java
@MappedSuperclass
@Getter
public abstract class SoftDeleteEntity extends BaseTimeEntity {

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    public void delete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
    }
}
```

**사용:**

```java
@Entity
@Where(clause = "deleted = false")  // 기본 조회 시 삭제된 것 제외
public class Member extends SoftDeleteEntity {
    @Id @GeneratedValue
    private Long id;
    private String name;
}

// Service
public void deleteMember(Long id) {
    Member member = memberRepository.findById(id)
        .orElseThrow();
    member.delete();  // 실제 삭제 대신 플래그만 변경
}
```

### 패턴 4️⃣: ID 통합 관리

```java
@MappedSuperclass
@Getter
public abstract class BaseIdEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
```

**사용:**

```java
@Entity
public class Product extends BaseIdEntity {
    // id는 이미 상속됨
    private String name;
    private int price;
}

@Entity
public class Category extends BaseIdEntity {
    // id는 이미 상속됨
    private String name;
}
```

---

## 🛠️ Spring Data JPA Auditing 설정

### 1️⃣ Application에 @EnableJpaAuditing 추가

```java
@SpringBootApplication
@EnableJpaAuditing
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
```

### 2️⃣ BaseEntity에 @EntityListeners 추가

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)  // 필수!
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String updatedBy;
}
```

### 3️⃣ AuditorAware 구현 (생성자/수정자)

```java
@Component
public class LoginUserAuditorAware implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication =
            SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
            || !authentication.isAuthenticated()
            || authentication instanceof AnonymousAuthenticationToken) {
            return Optional.empty();
        }

        return Optional.of(authentication.getName());
    }
}
```

---

## 📊 언제 사용할까?

### ✅ @MappedSuperclass 사용이 적합한 경우

#### 1️⃣ 공통 필드가 여러 엔티티에 중복될 때

```java
// ❌ Before: 중복 코드
@Entity
public class Member {
    @Id private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Entity
public class Product {
    @Id private Long id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// ✅ After: BaseEntity 상속
@MappedSuperclass
public abstract class BaseEntity {
    @CreatedDate private LocalDateTime createdAt;
    @LastModifiedDate private LocalDateTime updatedAt;
}

@Entity
public class Member extends BaseEntity {
    @Id private Long id;
}

@Entity
public class Product extends BaseEntity {
    @Id private Long id;
}
```

#### 2️⃣ 감사(Audit) 정보 추적

```java
@MappedSuperclass
public abstract class AuditEntity {
    @CreatedDate private LocalDateTime createdAt;
    @CreatedBy private String createdBy;
    @LastModifiedDate private LocalDateTime updatedAt;
    @LastModifiedBy private String updatedBy;
}

// 모든 엔티티가 "누가, 언제" 수정했는지 자동 기록
```

#### 3️⃣ 공통 비즈니스 로직

```java
@MappedSuperclass
public abstract class BaseEntity {

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    // 공통 비즈니스 로직
    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public boolean isDeleted() {
        return this.deleted;
    }
}
```

---

### ❌ @MappedSuperclass 사용이 부적합한 경우

#### 1️⃣ 부모 타입으로 조회가 필요한 경우

```java
// ❌ @MappedSuperclass - 불가능
@MappedSuperclass
public abstract class Payment { }

@Entity
public class CreditCardPayment extends Payment { }

@Entity
public class CashPayment extends Payment { }

// ❌ 컴파일 에러!
List<Payment> payments = em.createQuery(
    "SELECT p FROM Payment p",
    Payment.class
).getResultList();

// ✅ @Entity + @Inheritance 사용
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Payment { }

// ✅ 가능
List<Payment> payments = em.createQuery(
    "SELECT p FROM Payment p",
    Payment.class
).getResultList();
```

#### 2️⃣ 상속 구조가 도메인 모델인 경우

```java
// ❌ @MappedSuperclass - 부적합
// 상품(Item)은 실제 도메인 개념이므로

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
public abstract class Item {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private int price;
}

@Entity
@DiscriminatorValue("BOOK")
public class Book extends Item {
    private String author;
    private String isbn;
}

@Entity
@DiscriminatorValue("ALBUM")
public class Album extends Item {
    private String artist;
}
```

#### 3️⃣ 부모에서 관계 매핑을 관리해야 하는 경우

```java
// ❌ @MappedSuperclass - 관계 매핑 불가능

// ✅ @Entity 사용
@Entity
public abstract class Post {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();
}

@Entity
public class Article extends Post {
    private String title;
    private String content;
}

@Entity
public class Question extends Post {
    private String question;
}
```

---

## 💼 실무 베스트 프랙티스

### 1️⃣ 계층적 BaseEntity 구조

```java
// 최상위: 시간 정보만
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {
    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}

// 중간: 생성자/수정자 추가
@MappedSuperclass
@Getter
public abstract class BaseEntity extends BaseTimeEntity {
    @CreatedBy
    @Column(nullable = false, updatable = false)
    private String createdBy;

    @LastModifiedBy
    @Column(nullable = false)
    private String updatedBy;
}

// 선택적 사용
@Entity
public class Post extends BaseEntity {  // 생성자/수정자 필요
    @Id @GeneratedValue
    private Long id;
}

@Entity
public class Log extends BaseTimeEntity {  // 시간만 필요
    @Id @GeneratedValue
    private Long id;
}
```

### 2️⃣ abstract 키워드 사용

```java
// ✅ Good: abstract으로 인스턴스화 방지
@MappedSuperclass
public abstract class BaseEntity {
    // ...
}

// ❌ Bad: concrete 클래스 (혼란 초래)
@MappedSuperclass
public class BaseEntity {
    // ...
}
```

### 3️⃣ 필드 접근 제어

```java
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    // protected: 자식만 접근 가능
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}
```

### 4️⃣ 공통 비즈니스 로직 추가

```java
@MappedSuperclass
@Getter
public abstract class BaseEntity {

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 공통 메서드
    public boolean isCreatedAfter(LocalDateTime time) {
        return createdAt.isAfter(time);
    }

    public boolean isRecentlyUpdated(long minutes) {
        return updatedAt.isAfter(
            LocalDateTime.now().minusMinutes(minutes)
        );
    }
}
```

---

## ⚠️ 주의사항

### 1️⃣ @MappedSuperclass는 엔티티가 아님

```java
// ❌ 불가능
BaseEntity base = new BaseEntity();  // abstract라 불가능
em.persist(base);

BaseEntity found = em.find(BaseEntity.class, 1L);  // 조회 불가

List<BaseEntity> list = em.createQuery(
    "SELECT b FROM BaseEntity b", BaseEntity.class
).getResultList();  // JPQL 불가
```

### 2️⃣ @Table 어노테이션 무시됨

```java
// ⚠️ @Table은 의미 없음
@MappedSuperclass
@Table(name = "base")  // 무시됨!
public abstract class BaseEntity {
    // ...
}
```

### 3️⃣ 관계 매핑 제한

```java
// ❌ @MappedSuperclass는 관계의 주인이 될 수 없음
@MappedSuperclass
public abstract class BaseEntity {
    @OneToMany  // 작동하지 않음!
    private List<Comment> comments;
}

// ✅ 자식 엔티티에서 정의
@Entity
public class Post extends BaseEntity {
    @OneToMany(mappedBy = "post")
    private List<Comment> comments;
}
```

---

## 🎯 핵심 요약

### @MappedSuperclass 특징

| 항목            | 내용                                     |
| --------------- | ---------------------------------------- |
| **정의**        | 공통 필드를 모아둔 상속 전용 부모 클래스 |
| **테이블 생성** | ❌ 부모 클래스는 테이블로 생성 안 됨     |
| **필드 상속**   | ✅ 자식 엔티티 테이블에 컬럼으로 포함    |
| **JPQL 조회**   | ❌ 불가능 (조회 대상 아님)               |
| **다형성**      | ❌ 지원 안 함                            |
| **주요 용도**   | BaseEntity, BaseTimeEntity, AuditEntity  |

### 사용 시기

```
✅ 공통 필드만 상속하면 될 때
✅ 감사(Audit) 정보 추적
✅ 시간 정보 통합 관리
✅ 소프트 삭제 구현

❌ 부모 타입으로 조회해야 할 때
❌ 도메인 모델 상속 관계일 때
❌ 관계 매핑이 부모에 필요할 때
```

---

## 📚 실무 예제 모음

### 완전한 BaseEntity 구현

```java
@MappedSuperclass
@Getter
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @CreatedBy
    @Column(nullable = false, updatable = false, length = 50)
    private String createdBy;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @LastModifiedBy
    @Column(nullable = false, length = 50)
    private String updatedBy;

    @Column(nullable = false)
    private boolean deleted = false;

    private LocalDateTime deletedAt;

    private String deletedBy;

    // 비즈니스 로직
    public void softDelete(String deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    public void restore() {
        this.deleted = false;
        this.deletedAt = null;
        this.deletedBy = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return id != null && id.equals(that.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
```

### 사용 예제

```java
@Entity
@Table(name = "members")
@Where(clause = "deleted = false")
@Getter
public class Member extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    protected Member() {}

    @Builder
    public Member(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.status = MemberStatus.ACTIVE;
    }
}
```

---

## 💡 한 줄 요약

> **@MappedSuperclass는 엔티티들의 공통 필드를 한 곳에 모아 상속하는 JPA의 코드 재사용 메커니즘입니다. 테이블은 생성되지 않고, 조회 대상도 아니며, 오직 필드 상속만을 위해 사용됩니다.**

---

## 🚀 마무리

Backend 개발자라면 @MappedSuperclass를 활용해:

- ✅ **중복 코드 제거**: 공통 필드 한 곳에서 관리
- ✅ **감사 추적**: 생성/수정 정보 자동 기록
- ✅ **유지보수성 향상**: 공통 로직 중앙화
- ✅ **일관성 확보**: 모든 엔티티에 동일한 규칙 적용

Spring Data JPA Auditing과 함께 사용하면 강력한 시너지를 발휘합니다! 💪
