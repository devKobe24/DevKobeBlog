---
title: 🗄️ 데이터베이스와 SQL 기초
published: true
tags:
    - Backend
    - Database
    - SQL
    - DBMS
    
date: 2026-02-07
thumbnail: /assets/img/thumbnail/database.jpg
---

# 🗄️ 데이터베이스와 SQL 기초

> Backend 개발자가 꼭 알아야 할 데이터베이스의 모든 것

## 💡 데이터베이스란?

**정의**: 구조화된 데이터의 집합

우리 일상의 모든 디지털 활동은 데이터베이스에 기록됩니다:
- 💬 카카오톡 메시지
- 📸 인스타그램 사진
- 🚇 교통카드 사용 내역
- ☕ 카페 결제 정보

---

## 🔍 DB vs DBMS

### 핵심 구분

```
DB (Database)          = 데이터 그 자체
DBMS (Database         = 데이터를 관리하는
      Management          소프트웨어
      System)
```

### 비유로 이해하기

| 개념 | 비유 |
|------|------|
| **DB** | 도서관의 책들 (데이터) |
| **DBMS** | 사서와 관리 시스템 (관리 도구) |
| **SQL** | 책을 찾는 방법 (질의 언어) |

---

## 🆚 파일 시스템 vs DBMS

### 진화 과정

```
1단계: 종이와 펜 📝
    ↓
2단계: 컴퓨터 파일 (Excel) 💾
    ↓
3단계: DBMS (MySQL, PostgreSQL) 🗄️
```

### 파일 시스템의 한계

#### Excel 예제: 판매 관리

```
[판매_오전.xlsx]  [판매_오후.xlsx]  [판매_야간.xlsx]
     A 직원            B 직원            C 직원
```

**문제점:**
- ❌ **동시 접근 불가**: 한 번에 1명만 수정 가능
- ❌ **데이터 불일치**: 중복 입력 or 누락
- ❌ **무결성 보장 안 됨**: 실수로 잘못된 시트에 입력
- ❌ **통합 조회 어려움**: 전체 합계 계산 복잡

**시나리오:**
```
문제 1: A 직원이 B 직원 파일에 실수로 입력
문제 2: 오전 판매 물건을 오후에 반품 → 누가 기록?
문제 3: 월말 합계 계산 시 금액 불일치
```

---

### DBMS의 해결책

```sql
-- 모든 직원이 동일한 DB 사용
-- 동시 접근 가능 + 트랜잭션으로 무결성 보장

-- 판매 기록
INSERT INTO sales (employee_id, product_id, amount, sale_time)
VALUES (1, 101, 5000, '2025-02-07 09:30:00');

-- 반품 처리 (원자적 연산)
BEGIN TRANSACTION;
  UPDATE sales SET status = 'RETURNED' WHERE id = 12345;
  UPDATE inventory SET quantity = quantity + 1 WHERE product_id = 101;
COMMIT;

-- 실시간 집계
SELECT 
    DATE(sale_time) as date,
    SUM(amount) as daily_total
FROM sales
WHERE status != 'RETURNED'
GROUP BY DATE(sale_time);
```

---

## 📊 파일 vs DBMS 비교표

| 특성 | 파일 시스템 (Excel) | DBMS (MySQL) |
|------|-------------------|-------------|
| **동시 접근** | ❌ 불가능 (1명만) | ✅ 가능 (다중 사용자) |
| **데이터 무결성** | ❌ 보장 안 됨 | ✅ 트랜잭션으로 보장 |
| **중복 방지** | ❌ 수동 관리 | ✅ 제약 조건으로 자동 |
| **백업/복구** | ❌ 수동 복사 | ✅ 자동 백업 지원 |
| **보안** | ❌ 파일 권한만 | ✅ 세밀한 권한 관리 |
| **확장성** | ❌ 데이터 많으면 느림 | ✅ 대용량 처리 최적화 |
| **적합한 용도** | 소규모, 개인 작업 | 프로덕션 서비스 |

---

## 🛠️ 주요 DBMS 종류

### 관계형 데이터베이스 (RDBMS)

| DBMS | 특징 | 주요 사용처 |
|------|------|----------|
| **MySQL** | 오픈소스, 빠름, 웹 친화적 | 스타트업, 웹 서비스 |
| **PostgreSQL** | 강력한 기능, 표준 준수 | 엔터프라이즈, 복잡한 쿼리 |
| **Oracle** | 상용, 고성능, 기업용 | 대기업, 금융권 |
| **MariaDB** | MySQL 호환, 오픈소스 | MySQL 대체 |
| **MS SQL Server** | Windows 친화적 | .NET 생태계 |

### NoSQL 데이터베이스

| DBMS | 타입 | 주요 사용처 |
|------|------|----------|
| **MongoDB** | Document | JSON 형태 데이터 |
| **Redis** | Key-Value | 캐싱, 세션 |
| **Cassandra** | Wide-Column | 대용량 분산 |

---

## 💻 Spring Boot에서의 DB 활용

### 1️⃣ 데이터베이스 연결 설정

```yaml
# application.yml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mydb
    username: root
    password: password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        format_sql: true
```

### 2️⃣ Entity 정의 (테이블 매핑)

```java
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String name;
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @Builder
    public User(String email, String name) {
        this.email = email;
        this.name = name;
    }
}
```

### 3️⃣ Repository (DB 접근 계층)

```java
public interface UserRepository extends JpaRepository<User, Long> {
    
    // 메서드 이름으로 쿼리 자동 생성
    Optional<User> findByEmail(String email);
    
    List<User> findByNameContaining(String keyword);
    
    // JPQL 직접 작성
    @Query("SELECT u FROM User u WHERE u.createdAt > :date")
    List<User> findRecentUsers(@Param("date") LocalDateTime date);
    
    // Native SQL 사용
    @Query(value = "SELECT * FROM users WHERE email LIKE %:domain", 
           nativeQuery = true)
    List<User> findByEmailDomain(@Param("domain") String domain);
}
```

### 4️⃣ Service (비즈니스 로직)

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    
    private final UserRepository userRepository;
    
    // 조회 (읽기 전용)
    public User findById(Long id) {
        return userRepository.findById(id)
            .orElseThrow(() -> new UserNotFoundException(id));
    }
    
    // 저장 (쓰기 작업)
    @Transactional
    public User create(UserCreateRequest request) {
        // 중복 체크 (DBMS의 UNIQUE 제약조건으로도 보장)
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateEmailException(request.getEmail());
        }
        
        User user = User.builder()
            .email(request.getEmail())
            .name(request.getName())
            .build();
            
        return userRepository.save(user);
    }
    
    // 수정 (트랜잭션으로 무결성 보장)
    @Transactional
    public User update(Long id, UserUpdateRequest request) {
        User user = findById(id);
        user.updateName(request.getName());
        return user;  // 변경 감지(Dirty Checking)로 자동 UPDATE
    }
    
    // 삭제
    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}
```

---

## 📝 SQL이란?

**SQL (Structured Query Language)**: DBMS와 대화하는 언어

### SQL의 역할

```
개발자 (Java) → SQL → DBMS (MySQL) → Database
```

**비유:**
- DBMS = 미국 (시스템)
- SQL = 영어 (언어)
- 미국에서 소통하려면 영어를 배워야 하듯, DBMS를 사용하려면 SQL을 배워야 함

### SQL의 종류

| 분류 | 역할 | 주요 명령어 | 예시 |
|------|------|----------|-----|
| **DDL** | 구조 정의 | CREATE, ALTER, DROP | 테이블 생성 |
| **DML** | 데이터 조작 | SELECT, INSERT, UPDATE, DELETE | 데이터 CRUD |
| **DCL** | 권한 제어 | GRANT, REVOKE | 사용자 권한 관리 |
| **TCL** | 트랜잭션 제어 | COMMIT, ROLLBACK | 작업 확정/취소 |

---

## 🎯 실무 예제: 주문 시스템

### 테이블 설계

```sql
-- 사용자 테이블
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 상품 테이블
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

-- 주문 테이블
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- 주문 상품 테이블 (다대다 관계)
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (product_id) REFERENCES products(id)
);
```

### 트랜잭션으로 무결성 보장

```java
@Service
@RequiredArgsConstructor
public class OrderService {
    
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    
    @Transactional
    public Order createOrder(Long userId, List<OrderItemRequest> items) {
        // 1. 재고 확인 및 차감
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow(() -> new ProductNotFoundException());
            
            if (product.getStock() < item.getQuantity()) {
                throw new InsufficientStockException();
            }
            
            product.decreaseStock(item.getQuantity());
        }
        
        // 2. 주문 생성
        Order order = Order.builder()
            .userId(userId)
            .status(OrderStatus.PENDING)
            .build();
        
        // 3. 주문 항목 추가
        for (OrderItemRequest item : items) {
            order.addItem(item.getProductId(), item.getQuantity(), item.getPrice());
        }
        
        return orderRepository.save(order);
        
        // 트랜잭션 성공 → COMMIT
        // 예외 발생 → ROLLBACK (재고 차감 취소)
    }
}
```

---

## 🔒 DBMS의 핵심 기능

### 1️⃣ 동시성 제어 (Concurrency Control)

```java
// 여러 사용자가 동시에 같은 상품 주문
// DBMS가 자동으로 순서 보장

User A: 재고 10개 → 5개 구매 → 재고 5개
User B: 재고 5개 → 3개 구매 → 재고 2개

// 파일 시스템이었다면?
// A와 B가 동시에 재고 10개 읽음
// 둘 다 구매 처리 → 재고 음수 발생!
```

### 2️⃣ 트랜잭션 (ACID 속성)

| 속성 | 의미 | 예시 |
|------|------|------|
| **A**tomicity | 원자성 (전부 or 전무) | 계좌이체: 출금+입금 둘 다 성공 or 둘 다 취소 |
| **C**onsistency | 일관성 (규칙 준수) | 재고는 항상 0 이상 |
| **I**solation | 격리성 (독립 실행) | 동시 트랜잭션이 서로 영향 안 줌 |
| **D**urability | 지속성 (영구 저장) | COMMIT 후 정전되어도 데이터 유지 |

```java
@Transactional
public void transferMoney(Long fromId, Long toId, int amount) {
    Account from = accountRepository.findById(fromId)
        .orElseThrow();
    Account to = accountRepository.findById(toId)
        .orElseThrow();
    
    from.withdraw(amount);  // 출금
    to.deposit(amount);     // 입금
    
    // 둘 다 성공 → COMMIT
    // 하나라도 실패 → ROLLBACK
}
```

### 3️⃣ 무결성 제약 조건

```sql
-- NOT NULL: 필수 입력
CREATE TABLE users (
    email VARCHAR(255) NOT NULL  -- 반드시 입력
);

-- UNIQUE: 중복 불가
CREATE TABLE users (
    email VARCHAR(255) UNIQUE  -- 중복 이메일 차단
);

-- FOREIGN KEY: 참조 무결성
CREATE TABLE orders (
    user_id BIGINT,
    FOREIGN KEY (user_id) REFERENCES users(id)  -- 존재하는 사용자만
);

-- CHECK: 범위 제한
CREATE TABLE products (
    price INT CHECK (price > 0),  -- 가격은 양수만
    stock INT CHECK (stock >= 0)  -- 재고는 0 이상
);
```

---

## 📈 DBMS 발전사

```
1973년
└─ E.F. Codd가 관계형 모델 이론 정립

1970년대 후반
└─ Oracle, IBM DB2 등장

1980년대
└─ SQL 표준화

1990년대
└─ MySQL, PostgreSQL 오픈소스 등장

2000년대
└─ NoSQL 등장 (MongoDB, Redis)

현재
└─ NewSQL, Cloud Database (AWS RDS, Azure SQL)
```

---

## 🎯 Backend 개발자를 위한 팁

### 1️⃣ ORM vs SQL

```java
// ORM (JPA) - 편리함
List<User> users = userRepository.findByNameContaining("Kim");

// SQL - 성능 최적화
@Query("""
    SELECT u 
    FROM User u 
    JOIN FETCH u.orders 
    WHERE u.name LIKE %:name%
""")
List<User> findByNameWithOrders(@Param("name") String name);
```

**원칙:**
- 🟢 CRUD는 ORM 활용
- 🟡 복잡한 조회는 JPQL
- 🔴 성능 critical한 부분은 Native SQL

### 2️⃣ 인덱스 활용

```sql
-- 인덱스 없음 → Full Table Scan
SELECT * FROM users WHERE email = 'test@example.com';

-- 인덱스 추가
CREATE INDEX idx_email ON users(email);

-- 조회 속도 10배↑ (100만 건 기준)
```

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class User { }
```

### 3️⃣ 연관관계 주의

```java
// ❌ N+1 문제 발생
@GetMapping("/users")
public List<UserResponse> getUsers() {
    List<User> users = userRepository.findAll();  // 1번 쿼리
    return users.stream()
        .map(user -> UserResponse.of(user, user.getOrders()))  // N번 쿼리
        .toList();
}

// ✅ Fetch Join으로 해결
@Query("SELECT u FROM User u JOIN FETCH u.orders")
List<User> findAllWithOrders();  // 1번 쿼리로 해결
```

---

## 📚 학습 로드맵

```
1단계: SQL 기초 문법
├─ SELECT, WHERE, JOIN
├─ INSERT, UPDATE, DELETE
└─ GROUP BY, HAVING, ORDER BY

2단계: DBMS 개념
├─ 트랜잭션 (ACID)
├─ 인덱스
└─ 정규화

3단계: JPA/Hibernate
├─ Entity 매핑
├─ 연관관계
└─ 영속성 컨텍스트

4단계: 실무 최적화
├─ 쿼리 튜닝
├─ N+1 문제 해결
└─ 커넥션 풀 관리
```

---

## 💡 핵심 요약

| 개념 | 정의 | 실무 적용 |
|------|------|----------|
| **DB** | 데이터 집합 | MySQL, PostgreSQL |
| **DBMS** | DB 관리 시스템 | 동시성, 트랜잭션 보장 |
| **SQL** | 질의 언어 | CRUD 작업 |
| **ACID** | 트랜잭션 속성 | 데이터 무결성 |
| **ORM** | 객체-DB 매핑 | JPA, Hibernate |

---

## 🚀 마무리

> **"DBMS는 파일 시스템의 한계를 극복하고, 대용량 데이터를 안전하게 관리하는 필수 도구"**

Backend 개발자라면:
- 📊 **DBMS 원리 이해**: ACID, 트랜잭션, 인덱스
- 💻 **SQL 능숙하게 사용**: 복잡한 쿼리 작성
- 🔧 **JPA 효과적 활용**: N+1 문제 해결
- ⚡ **성능 최적화**: 쿼리 튜닝, 인덱스 설계

데이터베이스는 모든 서비스의 심장입니다. 제대로 이해하고 활용하면 안정적이고 확장 가능한 시스템을 만들 수 있습니다! 💪
