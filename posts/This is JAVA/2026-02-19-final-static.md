---
title: 🔒 Java final 필드와 상수 — 변경 불가능한 값
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Class
    - instance
    - static
    - final
    - constant
    - ComputerScience

date: 2026-02-19
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔒 Java final 필드와 상수 — 변경 불가능한 값

> **핵심 개념 :** final은 '최종적'이란 뜻으로, 초기값이 저장되면 프로그램 실행 중 수정할 수 없다. 상수는 `static final`로 선언하여 객체마다 저장하지 않고 클래스에 고정한다.

---

## 1️⃣ final 필드 — 객체마다 다른 불변값

### 선언 방법

```java
final 타입 필드명 [= 초기값];
```

### 초기화 방법 (2가지만 가능)

| 방법 | 시점 | 사용 사례 |
|------|------|-----------|
| **① 필드 선언 시** | 클래스 작성 시 | 고정된 값 |
| **② 생성자에서** | 객체 생성 시 | 객체마다 다른 값 |

> ⚠️ 이 두 방법 외에는 초기화 불가 → 컴파일 에러 발생

### 예시 — 주민등록번호

```java
public class Korean {
    final String nation = "대한민국";  // ① 선언 시 초기화 (모든 객체 동일)
    final String ssn;                  // ② 생성자에서 초기화 (객체마다 다름)
    String name;                       // 일반 필드 (변경 가능)
    
    public Korean(String ssn, String name) {
        this.ssn = ssn;   // final 필드 초기화
        this.name = name;
    }
}
```

```java
Korean k1 = new Korean("123456-1234567", "김자바");

// ✅ 읽기 가능
System.out.println(k1.nation);  // 대한민국
System.out.println(k1.ssn);     // 123456-1234567

// ❌ final 필드 변경 불가 (컴파일 에러)
k1.nation = "USA";         // 에러!
k1.ssn = "123-12-1234";    // 에러!

// ✅ 일반 필드는 변경 가능
k1.name = "강자바";         // 가능
```

---

## 2️⃣ 상수 — 클래스에 고정된 불변값

### 특징

상수는 **객체마다 저장할 필요가 없고** 여러 값을 가져서도 안 되므로 `static final`로 선언한다.

| 구분 | final 필드 | 상수 (static final) |
|------|------------|---------------------|
| **키워드** | `final` | `static final` |
| **저장 위치** | 객체 (Heap) | 클래스 (메서드 영역) |
| **용도** | 객체마다 다른 불변값 | 모든 곳에서 공유하는 불변값 |
| **예시** | 주민등록번호 | 원주율(π), 지구 반지름 |

### 선언 방법

```java
static final 타입 상수명 = 초기값;
```

### 명명 규칙

```java
✅ 모두 대문자
✅ 단어 연결은 언더바(_)

static final double PI = 3.14159;
static final double EARTH_RADIUS = 6400;
static final double EARTH_SURFACE_AREA = 5.147185403641517E8;
```

### 초기화 방법 (2가지)

**① 선언 시 초기화 (일반적)**

```java
static final double PI = 3.14159;
```

**② 정적 블록에서 초기화 (복잡한 계산)**

```java
static final double EARTH_SURFACE_AREA;

static {
    EARTH_SURFACE_AREA = 4 * Math.PI * EARTH_RADIUS * EARTH_RADIUS;
}
```

---

## 3️⃣ 상수 사용 — 클래스명.상수명

```java
public class Earth {
    static final double EARTH_RADIUS = 6400;
    static final double EARTH_SURFACE_AREA;
    
    static {
        EARTH_SURFACE_AREA = 4 * Math.PI * EARTH_RADIUS * EARTH_RADIUS;
    }
}
```

```java
// 클래스명으로 접근
System.out.println("지구 반지름: " + Earth.EARTH_RADIUS + "km");
System.out.println("지구 표면적: " + Earth.EARTH_SURFACE_AREA + "km²");
```

---

## 📌 핵심 요약

| 개념 | 키워드 | 초기화 시점 | 저장 위치 | 용도 |
|------|--------|-------------|-----------|------|
| **final 필드** | `final` | 선언 시 or 생성자 | Heap (객체) | 객체마다 다른 불변값 |
| **상수** | `static final` | 선언 시 or 정적 블록 | 메서드 영역 | 모든 곳에서 공유하는 불변값 |

### 규칙 정리

```
final 필드
  ├── 초기화: 선언 시 or 생성자에서
  ├── 변경: 불가능
  └── 예시: nation, ssn

상수 (static final)
  ├── 초기화: 선언 시 or 정적 블록
  ├── 명명: 대문자 + 언더바
  ├── 접근: 클래스명.상수명
  └── 예시: PI, EARTH_RADIUS
```
