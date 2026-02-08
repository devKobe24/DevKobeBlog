---
title: 🗄️ 데이터베이스와 DBMS
published: true
tags:
    - Backend
    - Database
    - SQL
    - DBMS
    
date: 2026-02-09
thumbnail: /assets/img/thumbnail/database.jpg
---

# 🗄️ 데이터베이스와 DBMS

> 모든 애플리케이션의 심장 - 데이터를 저장하고 관리하는 시스템

## 💡 핵심 개념

### 데이터베이스(Database, DB)

**정의**: 구조화된 데이터의 집합

**실생활 예시:**

- 💬 카카오톡 메시지
- 📸 인스타그램 사진
- 🚇 교통카드 사용 내역
- ☕ 카페 결제 정보

### DBMS (Database Management System)

**정의**: 데이터베이스를 관리하고 운영하는 소프트웨어

```
DB (데이터)  +  MS (관리 시스템)  =  DBMS
```

---

## 🆚 파일 시스템 vs DBMS

### 파일 시스템의 한계

#### Excel 예시

```
[판매_오전.xlsx]  [판매_오후.xlsx]  [판매_야간.xlsx]
     A 직원            B 직원            C 직원
```

**문제점:**

- ❌ **동시 접근 불가**: 한 번에 1명만 수정 가능
- ❌ **데이터 불일치**: 중복 입력, 누락 발생
- ❌ **무결성 보장 안 됨**: A 직원이 실수로 B 파일에 입력
- ❌ **통합 조회 어려움**: 3개 파일을 수동으로 합쳐야 함

**시나리오:**

```
오전 판매: A 직원이 노트북 판매 기록
오후 반품: B 직원이 노트북 반품 접수
❓ 누가 기록해야 할까?
→ 두 명 모두 기록 → 중복
→ 아무도 기록 안 함 → 누락
→ 월말 합계 불일치!
```

### DBMS의 해결책

```java
// 모든 직원이 동일한 DB 사용
@Service
@Transactional
public class SalesService {

    private final SalesRepository salesRepository;

    // 판매 기록 (동시성 보장)
    public void recordSale(SaleRequest request) {
        Sale sale = Sale.builder()
            .product(request.getProduct())
            .amount(request.getAmount())
            .employee(getCurrentEmployee())
            .saleTime(LocalDateTime.now())
            .build();

        salesRepository.save(sale);
    }

    // 반품 처리 (트랜잭션으로 무결성 보장)
    public void processReturn(Long saleId) {
        Sale sale = salesRepository.findById(saleId)
            .orElseThrow(() -> new SaleNotFoundException(saleId));

        sale.markAsReturned();
        // 자동으로 업데이트, 중복/누락 없음
    }

    // 실시간 집계 (모든 데이터 통합 조회)
    public DailySalesReport getDailySales(LocalDate date) {
        return salesRepository.findDailySales(date);
    }
}
```

### 비교 테이블

| 특성              | 파일 시스템 (Excel) | DBMS (MySQL)             |
| ----------------- | ------------------- | ------------------------ |
| **동시 접근**     | ❌ 불가능           | ✅ 다중 사용자 동시 접근 |
| **데이터 무결성** | ❌ 보장 안 됨       | ✅ 제약 조건으로 보장    |
| **트랜잭션**      | ❌ 지원 안 함       | ✅ ACID 보장             |
| **중복 방지**     | ❌ 수동 관리        | ✅ UNIQUE 제약           |
| **백업/복구**     | ❌ 수동 복사        | ✅ 자동 백업             |
| **보안**          | ❌ 파일 권한만      | ✅ 세밀한 권한 관리      |
| **확장성**        | ❌ 느려짐           | ✅ 대용량 최적화         |
| **적합 용도**     | 개인, 소규모        | 프로덕션 서비스          |

---

## 🏗️ DBMS의 종류

### 1️⃣ 관계형 DBMS (RDBMS)

**가장 많이 사용되는 DBMS**

#### 주요 제품

| DBMS              | 특징                      | 주요 사용처               | 가격 |
| ----------------- | ------------------------- | ------------------------- | ---- |
| **MySQL**         | 오픈소스, 빠름, 웹 친화적 | 스타트업, 웹 서비스       | 무료 |
| **PostgreSQL**    | 강력한 기능, 표준 준수    | 엔터프라이즈, 복잡한 쿼리 | 무료 |
| **Oracle**        | 고성능, 기업용            | 대기업, 금융권            | 유료 |
| **MariaDB**       | MySQL 호환, 오픈소스      | MySQL 대체                | 무료 |
| **MS SQL Server** | Windows 친화적            | .NET 생태계               | 유료 |

#### 관계형 DBMS의 특징

```
데이터를 테이블(Table) 형태로 저장
테이블 간 관계(Relation)를 통해 데이터 연결
```

### 2️⃣ NoSQL

**관계형이 아닌 다양한 형태의 데이터 저장**

| DBMS          | 타입        | 특징      | 사용처        |
| ------------- | ----------- | --------- | ------------- |
| **MongoDB**   | Document    | JSON 형태 | 유연한 스키마 |
| **Redis**     | Key-Value   | 초고속    | 캐싱, 세션    |
| **Cassandra** | Wide-Column | 분산 처리 | 대용량 데이터 |
| **Neo4j**     | Graph       | 관계 중심 | 소셜 네트워크 |

---

## 📊 관계형 DBMS의 구조

### 테이블(Table)

**정의**: 데이터를 저장하는 기본 단위

#### 테이블 예시: 회원(Member)

```
회원 테이블
┌────┬─────────────┬────────┬─────────────────┐
│ ID │    이름      │  나이   │     이메일       │
├────┼─────────────┼────────┼─────────────────┤
│ 1  │   홍길동     │   25   │ hong@example.com │
│ 2  │   김철수     │   30   │ kim@example.com  │
│ 3  │   이영희     │   28   │ lee@example.com  │
│ 4  │   박민수     │   35   │ park@example.com │˿ 행(Row)
└────┴─────────────┴────────┴─────────────────┘
   ↑        ↑          ↑            ↑
  열(Column)
         
```

### 용어 정리

| 용어                     | 설명                   | 별칭                               |
| ------------------------ | ---------------------- | ---------------------------------- |
| **테이블(Table)**        | 데이터를 저장하는 구조 | 릴레이션(Relation), 엔티티(Entity) |
| **행(Row)**              | 하나의 데이터          | 레코드(Record), 튜플(Tuple)        |
| **열(Column)**           | 데이터 항목            | 필드(Field), 속성(Attribute)       |
| **기본 키(Primary Key)** | 행을 고유하게 식별     | PK                                 |
| **외래 키(Foreign Key)** | 다른 테이블 참조       | FK                                 |

---

## 💻 Spring Boot에서의 RDBMS

### 1️⃣ 테이블 구조 설계

```sql
-- 회원 테이블
CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    age INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 주문 테이블
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 주문 상품 테이블
CREATE TABLE order_items (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    product_name VARCHAR(200) NOT NULL,
    quantity INT NOT NULL,
    price INT NOT NULL,
    FOREIGN KEY (order_id) REFERENCES orders(id)
);
```

### 2️⃣ JPA 엔티티로 매핑

```java
// 회원 엔티티 = members 테이블
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;  // id 컬럼

    @Column(nullable = false, length = 100)
    private String name;  // name 컬럼

    @Column(nullable = false, unique = true, length = 100)
    private String email;  // email 컬럼

    @Column(nullable = false)
    private int age;  // age 컬럼

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;  // created_at 컬럼

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;  // updated_at 컬럼

    @OneToMany(mappedBy = "member")
    private List<Order> orders = new ArrayList<>();  // 1:N 관계
}

// 주문 엔티티 = orders 테이블
@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // 외래 키
    private Member member;

    @Column(nullable = false)
    private int totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
```

### 3️⃣ DB 연결 설정

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
      ddl-auto: update # create, update, validate, none
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQLDialect
```

---

## 📝 SQL (Structured Query Language)

### SQL이란?

**정의**: RDBMS와 소통하는 언어

```
개발자 (Java) → SQL → DBMS (MySQL) → Database
```

### SQL의 종류

| 분류    | 역할        | 주요 명령어                    | 예시           |
| ------- | ----------- | ------------------------------ | -------------- |
| **DDL** | 구조 정의   | CREATE, ALTER, DROP            | 테이블 생성    |
| **DML** | 데이터 조작 | SELECT, INSERT, UPDATE, DELETE | 데이터 CRUD    |
| **DCL** | 권한 제어   | GRANT, REVOKE                  | 사용자 권한    |
| **TCL** | 트랜잭션    | COMMIT, ROLLBACK               | 작업 확정/취소 |

### 기본 SQL 문법

```sql
-- 조회 (SELECT)
SELECT id, name, email
FROM members
WHERE age >= 20
ORDER BY name ASC;

-- 삽입 (INSERT)
INSERT INTO members (name, email, age, created_at, updated_at)
VALUES ('홍길동', 'hong@example.com', 25, NOW(), NOW());

-- 수정 (UPDATE)
UPDATE members
SET age = 26, updated_at = NOW()
WHERE id = 1;

-- 삭제 (DELETE)
DELETE FROM members
WHERE id = 1;

-- 조인 (JOIN)
SELECT m.name, o.total_amount
FROM members m
JOIN orders o ON m.id = o.member_id
WHERE o.status = 'COMPLETED';
```

---

## 🔧 Spring Boot에서 SQL 사용

### 1️⃣ Spring Data JPA (Repository)

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    // 메서드 이름으로 쿼리 자동 생성
    Optional<Member> findByEmail(String email);

    List<Member> findByAgeGreaterThanEqual(int age);

    // JPQL
    @Query("SELECT m FROM Member m WHERE m.name LIKE %:keyword%")
    List<Member> searchByName(@Param("keyword") String keyword);

    // Native SQL
    @Query(value = "SELECT * FROM members WHERE age BETWEEN :min AND :max",
           nativeQuery = true)
    List<Member> findByAgeRange(@Param("min") int min, @Param("max") int max);
}
```

### 2️⃣ JdbcTemplate (순수 SQL)

```java
@Repository
@RequiredArgsConstructor
public class MemberJdbcRepository {

    private final JdbcTemplate jdbcTemplate;

    public Member findById(Long id) {
        String sql = "SELECT * FROM members WHERE id = ?";
        return jdbcTemplate.queryForObject(sql,
            (rs, rowNum) -> Member.builder()
                .id(rs.getLong("id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .age(rs.getInt("age"))
                .build(),
            id
        );
    }

    public void save(Member member) {
        String sql = """
            INSERT INTO members (name, email, age, created_at, updated_at)
            VALUES (?, ?, ?, ?, ?)
            """;
        jdbcTemplate.update(sql,
            member.getName(),
            member.getEmail(),
            member.getAge(),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }
}
```

### 3️⃣ MyBatis (XML 매핑)

```xml
<!-- MemberMapper.xml -->
<mapper namespace="com.example.mapper.MemberMapper">
    <select id="findById" resultType="Member">
        SELECT id, name, email, age
        FROM members
        WHERE id = #{id}
    </select>

    <insert id="save">
        INSERT INTO members (name, email, age, created_at, updated_at)
        VALUES (#{name}, #{email}, #{age}, NOW(), NOW())
    </insert>
</mapper>
```

```java
@Mapper
public interface MemberMapper {
    Member findById(Long id);
    void save(Member member);
}
```

---

## 📌 SQL 표준

### 표준 SQL

**정의**: 국제표준화기구(ISO)에서 정한 SQL 규격

```
표준 SQL (공통)
├─ Oracle (PL/SQL) ← 표준 + Oracle 전용
├─ MySQL (SQL) ← 표준 + MySQL 전용
└─ MS SQL Server (T-SQL) ← 표준 + MSSQL 전용
```

### DBMS별 차이점 예시

| 기능            | MySQL            | PostgreSQL  | Oracle         |
| --------------- | ---------------- | ----------- | -------------- |
| **자동 증가**   | `AUTO_INCREMENT` | `SERIAL`    | `SEQUENCE`     |
| **문자열 결합** | `CONCAT()`       | `\|\|`      | `\|\|`         |
| **LIMIT**       | `LIMIT 10`       | `LIMIT 10`  | `ROWNUM <= 10` |
| **날짜 형식**   | `DATE_FORMAT()`  | `TO_CHAR()` | `TO_CHAR()`    |

```sql
-- MySQL
SELECT * FROM members LIMIT 10;

-- PostgreSQL
SELECT * FROM members LIMIT 10;

-- Oracle
SELECT * FROM members WHERE ROWNUM <= 10;

-- SQL Server
SELECT TOP 10 * FROM members;
```

---

## 🎯 실무 예제: 주문 시스템

### 테이블 설계

```sql
-- 회원
CREATE TABLE members (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL
);

-- 상품
CREATE TABLE products (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(200) NOT NULL,
    price INT NOT NULL,
    stock INT NOT NULL
);

-- 주문
CREATE TABLE orders (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    member_id BIGINT NOT NULL,
    total_amount INT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    FOREIGN KEY (member_id) REFERENCES members(id)
);

-- 주문 상품
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

### Service 구현

```java
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public Order createOrder(Long memberId, List<OrderItemRequest> items) {
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
        Order order = Order.create(memberId);

        // 3. 주문 상품 추가
        for (OrderItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                .orElseThrow();
            order.addItem(product, item.getQuantity());
        }

        // 4. 저장
        return orderRepository.save(order);

        // 트랜잭션 성공 → COMMIT
        // 예외 발생 → ROLLBACK (재고 차감 취소)
    }
}
```

---

## 💡 핵심 요약

### DBMS의 3대 특징

```
1️⃣ 동시성: 여러 사용자 동시 접근
2️⃣ 무결성: 데이터 일관성 보장
3️⃣ 트랜잭션: ACID 속성으로 안정성
```

### 관계형 DBMS 구조

```
데이터베이스 (Database)
└─ 테이블 (Table)
   ├─ 열 (Column) ← 데이터 항목
   └─ 행 (Row) ← 데이터 레코드
```

### SQL의 역할

```
Java 코드 → JPA/JDBC → SQL → MySQL → 데이터
```

---

## 📚 학습 로드맵

```
1단계: RDBMS 개념 이해
├─ 테이블, 행, 열
├─ 기본 키, 외래 키
└─ 관계(1:1, 1:N, N:M)

2단계: SQL 기초 문법
├─ SELECT, WHERE, JOIN
├─ INSERT, UPDATE, DELETE
└─ GROUP BY, ORDER BY

3단계: JPA/Hibernate
├─ 엔티티 매핑
├─ 연관관계
└─ 영속성 컨텍스트

4단계: 실무 최적화
├─ 인덱스
├─ 쿼리 튜닝
└─ 트랜잭션 관리
```

---

## 🚀 마무리

> **"DBMS는 애플리케이션의 심장이며, SQL은 DBMS와 소통하는 언어입니다"**

Backend 개발자라면:

- 🗄️ **RDBMS 이해**: 테이블, 관계, 제약 조건
- 💾 **SQL 숙련**: 복잡한 쿼리 작성 능력
- 🌱 **JPA 활용**: ORM으로 생산성 향상
- ⚡ **성능 최적화**: 인덱스, 쿼리 튜닝

데이터베이스를 제대로 이해하면 안정적이고 확장 가능한 시스템을 만들 수 있습니다! 💪
