---
title: ✅ 참과 거짓, 단 두 가지만 저장한다
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Type
    - ComputerScience
    - PrimitiveType
    - boolean

date: 2026-02-23
thumbnail: /assets/img/thumbnail/java.jpg
---

# ✅ 참과 거짓, 단 두 가지만 저장한다
### — boolean 타입 완전 정복

> **핵심 요약:** `boolean`은 `true` 또는 `false` 단 두 가지 값만 저장하는 타입이다.  
> 프로그램의 **상태를 표현**하고, **조건문과 제어 흐름**을 결정하는 데 핵심적으로 사용된다.

---

## 1️⃣ boolean 타입 기본

```java
boolean isRunning = true;
boolean isStopped = false;
```

| 리터럴 | 의미 | 메모리 크기 |
|:------:|------|:-----------:|
| `true` | 참 (조건 성립) | 1byte |
| `false` | 거짓 (조건 불성립) | 1byte |

> 💡 Java의 `boolean`은 `0`과 `1`로 표현하지 않는다.  
> C/C++과 달리 **`true`/`false` 리터럴만 허용**하며, 정수값 대입 시 컴파일 에러가 발생한다.

```java
boolean flag = 1;     // ❌ 컴파일 에러 (C언어와 다름!)
boolean flag = true;  // ✅
```

---

## 2️⃣ boolean의 주된 용도 — 상태 표현과 제어 흐름

### 상태값 저장 후 조건문에 활용

```java
boolean stop = true;

if (stop) {
    System.out.println("중지합니다.");  // ← stop이 true면 실행
} else {
    System.out.println("시작합니다.");  // ← stop이 false면 실행
}
// 출력: 중지합니다.
```

---

## 3️⃣ 비교 연산식 → boolean 결과

비교 연산과 논리 연산의 결과는 항상 `true` 또는 `false`이므로 boolean 변수에 직접 저장할 수 있다.

### 비교 연산자

| 연산식 | 의미 | x = 10일 때 결과 |
|--------|------|:---------------:|
| `x == 20` | x의 값이 20인가? | `false` |
| `x != 20` | x의 값이 20이 아닌가? | `true` |
| `x > 20` | x의 값이 20보다 큰가? | `false` |
| `x >= 10` | x의 값이 10 이상인가? | `true` |
| `x < 20` | x의 값이 20보다 작은가? | `true` |

### 논리 연산자 (조건 결합)

| 연산식 | 의미 | x = 10일 때 결과 |
|--------|------|:---------------:|
| `0 < x && x < 20` | x가 0보다 크고, 20보다 작은가? | `true` |
| `x < 0 \|\| x > 200` | x가 0보다 작거나, 200보다 큰가? | `false` |

<img src="https://github.com/devKobe24/images2/blob/main/boolean-type-1.png?raw=true"/>

```java
int x = 10;

boolean result1 = (x == 20);            // false
boolean result2 = (x != 20);            // true
boolean result3 = (x > 20);             // false
boolean result4 = (0 < x && x < 20);    // true  (범위 체크)
boolean result5 = (x < 0 || x > 200);  // false

System.out.println("result1: " + result1);  // false
System.out.println("result2: " + result2);  // true
System.out.println("result4: " + result4);  // true
System.out.println("result5: " + result5);  // false
```

---

## 4️⃣ 실무 활용 패턴

### 메서드 반환값으로 상태 표현

```java
// 반환 타입이 boolean인 메서드
public boolean isAdult(int age) {
    return age >= 18;
}

public boolean isEmpty(String str) {
    return str == null || str.isEmpty();
}

// 사용
if (isAdult(20)) {
    System.out.println("성인입니다.");
}
```

### 플래그(Flag) 변수로 루프 제어

```java
boolean found = false;
int[] numbers = {3, 7, 2, 9, 5};

for (int num : numbers) {
    if (num == 9) {
        found = true;
        break;
    }
}

System.out.println("9를 찾았나요? " + found);  // true
```

### 네이밍 컨벤션 — boolean 변수는 `is`, `has`, `can`으로 시작

```java
boolean isLoggedIn  = true;   // 로그인 상태인가?
boolean hasPermission = false; // 권한이 있는가?
boolean canEdit = true;        // 수정 가능한가?
boolean isEmpty = false;       // 비어있는가?
```

> 💡 `is`, `has`, `can` 접두사를 붙이면 **변수 이름만 봐도 boolean임을 즉시 알 수 있다.**  
> Java 표준 네이밍 컨벤션이자 가독성을 높이는 실무 관행이다.

---

## 5️⃣ ⚠️ 주의사항 — `==` vs `equals()`

```java
// 원시 타입 boolean: == 사용 ✅
boolean a = true;
boolean b = true;
System.out.println(a == b);  // true

// 래퍼 클래스 Boolean: equals() 사용 ✅
Boolean x = Boolean.TRUE;
Boolean y = Boolean.TRUE;
System.out.println(x.equals(y));  // true
System.out.println(x == y);       // true (캐싱으로 같은 객체지만, 일반적으로 equals 권장)
```

---

## 6️⃣ 핵심 정리

```
boolean = true 또는 false, 단 두 가지
        → 상태 표현 + 조건문 제어의 핵심 타입
```

| 개념 | 한 줄 요약 |
|------|-----------|
| **boolean** | `true`/`false`만 저장, 1byte, 정수 대입 불가 |
| **비교 연산** | `==`, `!=`, `>`, `<` 등의 결과가 boolean |
| **논리 연산** | `&&`(AND), `\|\|`(OR)로 조건 결합 |
| **네이밍** | `is`, `has`, `can` 접두사로 가독성 향상 |
| **활용** | 조건문, 루프 제어, 메서드 반환, 상태 플래그 |
