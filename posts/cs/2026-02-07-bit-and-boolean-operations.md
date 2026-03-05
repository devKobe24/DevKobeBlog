---
title: 💾 비트와 불리언 연산
published: true
tags:
    - cs
    - 컴퓨터 구조
    - Programming

date: 2026-02-07
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 💾 비트와 불리언 연산

> 컴퓨터가 생각하는 방식 - 0과 1로 모든 것을 표현하기

## 🔢 비트(Bit)란?

**비트(Bit)** = **Bi**nary digi**t** (2진 숫자)

**핵심 개념:**
- 컴퓨터의 가장 작은 데이터 단위
- 오직 **2가지 상태**만 가능: `0` 또는 `1`
- 자연어의 '문자'와 대응되는 컴퓨터의 기본 단위

### 2진법의 표현

```
비트는 두 가지 상태로 표현됨:
├─ 0 (거짓, False, OFF, 낮음)
└─ 1 (참, True, ON, 높음)
```

**물리적 구현:**
- 💡 전기: 꺼짐(0) / 켜짐(1)
- 🧲 자기: N극(0) / S극(1)
- ⚡ 전압: 낮음(0) / 높음(1)

---

## 🎯 불리언(Boolean) 값

### 정의

**Boolean**: 참(True) 또는 거짓(False) 두 가지 값만 가지는 데이터 타입

```java
boolean isRaining = true;    // 비가 온다
boolean isCold = false;      // 춥지 않다
```

### 예/아니오 질문으로 표현 가능

| 질문 | Boolean 값 |
|------|-----------|
| "비가 오는가?" | `true` / `false` |
| "사용자가 로그인했는가?" | `true` / `false` |
| "재고가 있는가?" | `true` / `false` |
| "나이가 19세 이상인가?" | `true` / `false` |

**표현 불가능한 질문:**
- ❌ "이름이 무엇인가?" → String 필요
- ❌ "나이가 몇 살인가?" → int 필요
- ❌ "어디에 사는가?" → String 필요

---

## 🧮 불리언 연산자 4가지

### 1️⃣ NOT (논리 부정)

**정의**: 값을 반대로 뒤집음

```java
boolean isRaining = true;
boolean isNotRaining = !isRaining;  // false

// 실무 예제
boolean isLoggedIn = true;
if (!isLoggedIn) {  // 로그인하지 않았다면
    return "로그인이 필요합니다";
}
```

**진리표:**

| 입력 | NOT 입력 |
|------|---------|
| T    | F       |
| F    | T       |

---

### 2️⃣ AND (논리곱)

**정의**: 모든 조건이 참일 때만 참

```java
boolean isCold = true;
boolean isRaining = true;
boolean wearCoat = isCold && isRaining;  // true

// 실무 예제: 주문 가능 여부
boolean hasStock = product.getStock() > 0;
boolean isPaid = order.getStatus() == OrderStatus.PAID;
boolean canShip = hasStock && isPaid;  // 둘 다 true여야 배송 가능
```

**진리표:**

| A | B | A AND B |
|-------|-------|---------|
| T | T | T       |
| T | F | F       |
| F | T | F       |
| F | F | F       |

**Java 연산자:**
- `&&`: 논리 AND (Short-circuit)
- `&`: 비트 AND (모든 조건 평가)

```java
// Short-circuit: 첫 번째가 false면 두 번째 평가 안 함
if (user != null && user.isActive()) {  // NPE 방지
    // user가 null이면 isActive() 호출 안 함
}

// 비트 AND: 무조건 모두 평가
if (user != null & user.isActive()) {  // NPE 가능!
    // user가 null이어도 isActive() 호출 → NullPointerException
}
```

---

### 3️⃣ OR (논리합)

**정의**: 하나라도 참이면 참

```java
boolean isCold = true;
boolean isRaining = false;
boolean wearCoat = isCold || isRaining;  // true

// 실무 예제: 할인 적용 가능 여부
boolean isVIP = user.getGrade() == Grade.VIP;
boolean hasEventCoupon = user.hasCoupon("EVENT2025");
boolean canDiscount = isVIP || hasEventCoupon;  // 둘 중 하나만 true면 할인
```

**진리표:**

| A | B | A OR B |
|-------|-------|--------|
| T | T | T      |
| T | F | T      |
| F | T | T      |
| F | F | F      |

**Java 연산자:**
- `||`: 논리 OR (Short-circuit)
- `|`: 비트 OR (모든 조건 평가)

```java
// Short-circuit: 첫 번째가 true면 두 번째 평가 안 함
if (user.isAdmin() || user.hasPermission("WRITE")) {
    // admin이면 hasPermission() 호출 안 함 (성능 최적화)
}
```

---

### 4️⃣ XOR (배타적 논리합)

**정의**: 두 값이 **다를 때만** 참

```java
boolean a = true;
boolean b = false;
boolean result = a ^ b;  // true (서로 다름)

// 실무 예제: 토글 기능
boolean isLiked = post.isLikedByUser(userId);
post.setLiked(isLiked ^ true);  // 좋아요 상태 반전
```

**진리표:**

| A | B | A XOR B |
|-------|-------|---------|
| T | T | F       |
| T | F | T       |
| F | T | T       |
| F | F | F       |

**XOR의 특징:**
```java
// XOR은 다른 연산으로 표현 가능
boolean xor = (a || b) && !(a && b);

// 비트 연산에서 자주 사용
int encrypted = data ^ key;  // 암호화
int decrypted = encrypted ^ key;  // 복호화 (같은 key로 다시 XOR)
```

---

## 📊 진리표 (Truth Table)

### 진리표란?

모든 가능한 입력 조합에 대한 출력을 정리한 표

### 전체 연산자 비교

| A | B | NOT A | A AND B | A OR B | A XOR B |
|-------|-------|-------|---------|--------|---------|
| T | T | F     | T       | T      | F       |
| T | F | F     | F       | T      | T       |
| F | T | T     | F       | T      | T       |
| F | F | T     | F       | F      | F       |

---

## 💡 실무 활용 예제

### 1️⃣ 조건문에서의 활용

```java
@Service
public class OrderService {
    
    public boolean canPlaceOrder(User user, Product product, int quantity) {
        // 여러 조건을 AND로 결합
        boolean userValid = user != null && user.isActive();
        boolean productAvailable = product.getStock() >= quantity;
        boolean priceValid = product.getPrice() > 0;
        
        return userValid && productAvailable && priceValid;
    }
    
    public boolean isEligibleForDiscount(User user) {
        // 여러 조건을 OR로 결합
        boolean isVIP = user.getGrade() == Grade.VIP;
        boolean isFirstPurchase = user.getOrderCount() == 0;
        boolean hasEventCoupon = user.hasCoupon("WELCOME");
        
        return isVIP || isFirstPurchase || hasEventCoupon;
    }
}
```

### 2️⃣ 권한 체크

```java
@RestController
public class AdminController {
    
    @GetMapping("/admin/users")
    public ResponseEntity<List<User>> getUsers(
        @AuthenticationPrincipal User currentUser
    ) {
        // AND: 둘 다 충족해야 함
        if (currentUser.isAdmin() && currentUser.isActive()) {
            return ResponseEntity.ok(userService.getAllUsers());
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
    
    @PostMapping("/posts/{id}/delete")
    public ResponseEntity<Void> deletePost(
        @PathVariable Long id,
        @AuthenticationPrincipal User currentUser
    ) {
        Post post = postService.findById(id);
        
        // OR: 둘 중 하나만 충족하면 됨
        boolean canDelete = currentUser.isAdmin() 
                         || post.getAuthorId().equals(currentUser.getId());
        
        if (canDelete) {
            postService.delete(id);
            return ResponseEntity.ok().build();
        }
        
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
}
```

### 3️⃣ 유효성 검사

```java
@Service
public class ValidationService {
    
    public boolean isValidUser(UserRequest request) {
        // 모든 조건이 true여야 유효한 사용자
        boolean hasEmail = request.getEmail() != null 
                        && !request.getEmail().isEmpty();
        boolean hasPassword = request.getPassword() != null 
                           && request.getPassword().length() >= 8;
        boolean hasName = request.getName() != null 
                       && !request.getName().isEmpty();
        boolean isAdult = request.getAge() >= 19;
        
        return hasEmail && hasPassword && hasName && isAdult;
    }
    
    public boolean canAccessContent(User user, Content content) {
        // NOT 활용
        boolean isNotBlocked = !user.isBlocked();
        boolean isNotExpired = !content.isExpired();
        
        // OR + NOT 조합
        boolean hasPermission = content.isPublic() 
                             || content.getAuthorId().equals(user.getId());
        
        return isNotBlocked && isNotExpired && hasPermission;
    }
}
```

---

## 🎭 드모르간의 법칙 (De Morgan's Law)

### 법칙 정의

```
NOT (A AND B) = (NOT A) OR (NOT B)
NOT (A OR B)  = (NOT A) AND (NOT B)
```

### 진리표로 증명

| A | B | A AND B | NOT(A AND B) | NOT A | NOT B | (NOT A) OR (NOT B) |
|-------|-------|---------|--------------|-------|-------|--------------------|
| T | T | T       | F            | F     | F     | F                  |
| T | F | F       | T            | F     | T     | T                  |
| F | T | F       | T            | T     | F     | T                  |
| F | F | F       | T            | T     | T     | T                  |

**마지막 두 열이 동일함을 확인!**

### 실무 활용

```java
// 원본 조건: 코트를 입어야 하는 경우
boolean shouldWearCoat = isCold || isRaining;

// 드모르간의 법칙 적용
// "코트를 입지 않아도 되는 경우"로 변환
boolean shouldNotWearCoat = !isCold && !isRaining;
boolean shouldWearCoat2 = !shouldNotWearCoat;

// 실제 사용 예제
@Service
public class AccessControlService {
    
    // 긍정 논리 (Positive Logic)
    public boolean canAccess(User user, Resource resource) {
        return user.isAdmin() || resource.isPublic();
    }
    
    // 부정 논리 (Negative Logic) - 드모르간 적용
    public boolean cannotAccess(User user, Resource resource) {
        // NOT(admin OR public) = (NOT admin) AND (NOT public)
        return !user.isAdmin() && !resource.isPublic();
    }
    
    // 활용
    public void checkAccess(User user, Resource resource) {
        if (cannotAccess(user, resource)) {
            throw new AccessDeniedException("접근 권한이 없습니다");
        }
    }
}
```

### 조건문 단순화

```java
// ❌ 복잡한 조건
if (!(user.isActive() && user.isVerified())) {
    return "사용자 계정에 문제가 있습니다";
}

// ✅ 드모르간 적용으로 단순화
if (!user.isActive() || !user.isVerified()) {
    return "사용자 계정에 문제가 있습니다";
}

// ❌ 중첩된 NOT
if (!(product.isOutOfStock() || product.isDiscontinued())) {
    return "구매 가능";
}

// ✅ 드모르간 적용
if (!product.isOutOfStock() && !product.isDiscontinued()) {
    return "구매 가능";
}
```

---

## 🔧 비트 연산자 (Bitwise Operators)

### 정수에 대한 비트 연산

```java
int a = 5;   // 0101 (2진수)
int b = 3;   // 0011 (2진수)

int and = a & b;   // 0001 = 1 (AND)
int or  = a | b;   // 0111 = 7 (OR)
int xor = a ^ b;   // 0110 = 6 (XOR)
int not = ~a;      // 1010 = -6 (NOT, 2의 보수)

// 시프트 연산
int left  = a << 1;  // 1010 = 10 (왼쪽으로 1비트 = *2)
int right = a >> 1;  // 0010 = 2  (오른쪽으로 1비트 = /2)
```

### 실무 활용: 플래그(Flag) 관리

```java
public class Permission {
    // 비트 플래그로 권한 표현
    public static final int READ   = 1;  // 0001
    public static final int WRITE  = 2;  // 0010
    public static final int DELETE = 4;  // 0100
    public static final int ADMIN  = 8;  // 1000
    
    private int permissions = 0;
    
    // 권한 추가 (OR)
    public void grant(int permission) {
        permissions |= permission;
    }
    
    // 권한 제거 (AND + NOT)
    public void revoke(int permission) {
        permissions &= ~permission;
    }
    
    // 권한 확인 (AND)
    public boolean hasPermission(int permission) {
        return (permissions & permission) == permission;
    }
    
    // 사용 예제
    public static void main(String[] args) {
        Permission perm = new Permission();
        
        perm.grant(READ);         // 읽기 권한 부여
        perm.grant(WRITE);        // 쓰기 권한 부여
        
        System.out.println(perm.hasPermission(READ));   // true
        System.out.println(perm.hasPermission(DELETE)); // false
        
        perm.revoke(WRITE);       // 쓰기 권한 제거
        System.out.println(perm.hasPermission(WRITE));  // false
    }
}
```

---

## 💼 Spring Security에서의 불리언 연산

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http.authorizeHttpRequests(auth -> auth
            // AND: 인증되고 AND ADMIN 역할
            .requestMatchers("/admin/**")
                .access((authentication, context) -> 
                    new AuthorizationDecision(
                        authentication.get().isAuthenticated() 
                        && hasRole(authentication.get(), "ADMIN")
                    ))
            
            // OR: PUBLIC이거나 OR 인증됨
            .requestMatchers("/api/**")
                .access((authentication, context) ->
                    new AuthorizationDecision(
                        isPublicEndpoint(context) 
                        || authentication.get().isAuthenticated()
                    ))
            
            .anyRequest().authenticated()
        );
        
        return http.build();
    }
}
```

---

## 📚 복합 조건문 Best Practices

### 1️⃣ 명확한 변수명 사용

```java
// ❌ 나쁜 예
if (u.a && (u.b || !u.c)) { }

// ✅ 좋은 예
boolean isActive = user.isActive();
boolean isPremium = user.isPremium();
boolean isNotBlocked = !user.isBlocked();

if (isActive && (isPremium || isNotBlocked)) { }
```

### 2️⃣ Early Return으로 단순화

```java
// ❌ 복잡한 중첩
public void processOrder(Order order) {
    if (order != null) {
        if (order.isPaid()) {
            if (order.hasStock()) {
                // 처리 로직
            }
        }
    }
}

// ✅ Early Return
public void processOrder(Order order) {
    if (order == null) {
        return;
    }
    if (!order.isPaid()) {
        return;
    }
    if (!order.hasStock()) {
        return;
    }
    
    // 처리 로직 (들여쓰기 최소화)
}
```

### 3️⃣ 메서드 추출로 가독성 향상

```java
// ❌ 조건이 너무 길어서 읽기 어려움
if (user.getAge() >= 19 
    && user.isActive() 
    && !user.isBlocked() 
    && (user.isPremium() || user.hasEventCoupon())) {
    // ...
}

// ✅ 의미 있는 메서드로 분리
if (canAccessAdultContent(user)) {
    // ...
}

private boolean canAccessAdultContent(User user) {
    boolean isAdult = user.getAge() >= 19;
    boolean hasAccess = user.isActive() && !user.isBlocked();
    boolean hasPrivilege = user.isPremium() || user.hasEventCoupon();
    
    return isAdult && hasAccess && hasPrivilege;
}
```

---

## 🎯 핵심 요약

| 연산자 | 기호 | 의미 | Java 예제 |
|--------|------|------|----------|
| **NOT** | ! | 반대 | `!isActive` |
| **AND** | && | 모두 참 | `a && b` |
| **OR** | \|\| | 하나라도 참 | `a \|\| b` |
| **XOR** | ^ | 다를 때 참 | `a ^ b` |

### 드모르간의 법칙

```java
!(A && B) == !A || !B
!(A || B) == !A && !B
```

### Short-circuit 평가

```java
// && : 왼쪽이 false면 오른쪽 평가 안 함
if (user != null && user.isActive()) { }

// || : 왼쪽이 true면 오른쪽 평가 안 함
if (isAdmin() || hasPermission()) { }
```

---

## 💡 마무리

> **"불리언 연산은 프로그래밍의 기초이자 핵심"**

Backend 개발자라면:
- ✅ **조건문 작성**: if, while, for에서 필수
- ✅ **권한 체크**: 복잡한 권한 로직 구현
- ✅ **유효성 검사**: 입력 데이터 검증
- ✅ **필터링**: Stream API의 filter 조건
- ✅ **보안**: 인증/인가 로직

불리언 연산을 제대로 이해하면 복잡한 비즈니스 로직도 명확하게 표현할 수 있습니다! 💪
