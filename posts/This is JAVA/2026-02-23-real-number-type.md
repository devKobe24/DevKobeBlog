---
title: 🔢 소수점이 있는 숫자는 어떻게 저장될까?
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Type
    - ComputerScience
    - PrimitiveType
    - Float
    - Double

date: 2026-02-23
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔢 소수점이 있는 숫자는 어떻게 저장될까?
### — float과 double 완전 정복

> **핵심 요약:** Java에서 실수는 `float`과 `double` 두 타입으로 저장된다.  
> 둘 다 IEEE 754 부동 소수점 방식을 사용하며, `double`이 `float`보다 약 2배 정밀하다.

---

## 1️⃣ float vs double — 한눈에 비교

| 타입 | 메모리 크기 | 저장 범위 (양수 기준) | 유효 소수 자리 |
|------|:-----------:|---------------------|:------------:|
| `float` | 4byte (32bit) | 1.4 × 10⁻⁴⁵ ~ 3.4 × 10³⁸ | **7자리** |
| `double` | 8byte (64bit) | 4.9 × 10⁻³²⁴ ~ 1.8 × 10³⁰⁸ | **15자리** |

<img src="https://github.com/devKobe24/images2/blob/main/real-number-type-2.png?raw=true"/>

> 💡 **`double`이라는 이름의 유래?**  
> `float`보다 약 **2배(Double)의 정밀도**를 갖는다는 의미에서 붙여진 이름이다.

---

## 2️⃣ 부동 소수점(IEEE 754) — 실수를 비트로 표현하는 방법

Java는 **IEEE 754 표준**에 따라 실수를 다음 형식으로 저장한다.

<img src="https://github.com/devKobe24/images2/blob/main/real-number-type-3.png?raw=true"/>

```
실수 = 부호(+/-) × 가수(mantissa) × 10^지수(exponent)

예) -3.14 = (-) × 3.14 × 10⁰
    500.0 =  (+) × 5.0  × 10²
    0.002 =  (+) × 2.0  × 10⁻³
```

### 메모리 비트 구조

<img src="https://github.com/devKobe24/images2/blob/main/real-number-type-4.png?raw=true"/>

| 구성 요소 | float | double | 역할 |
|----------|:-----:|:------:|------|
| **부호 bit** | 1 | 1 | 0 = 양수, 1 = 음수 |
| **지수 (Exponent)** | 8bit | 11bit | 소수점 위치 결정 (표현 범위) |
| **가수 (Mantissa)** | 23bit | 52bit | 유효 숫자 결정 (정밀도) |
| **합계** | 32bit | 64bit | — |

> 💡 **지수 bit가 크면** → 더 크거나 더 작은 수를 표현 가능 (범위 확장)  
> **가수 bit가 크면** → 소수점 아래 더 많은 자릿수 표현 가능 (정밀도 향상)

---

## 3️⃣ 실수 리터럴 작성법

### 기본 10진수

```java
double x = 0.25;
double y = -3.14;
```

### e/E를 사용한 10의 거듭제곱 표기

```java
double x = 5e2;    // 5.0 × 10² = 500.0
double y = 0.12E-2; // 0.12 × 10⁻² = 0.0012
double z = 2e-3;   // 2.0 × 10⁻³ = 0.002
```

---

## 4️⃣ ⚠️ 주의사항

### 실수 리터럴의 기본 타입은 double

컴파일러는 실수 리터럴을 기본적으로 **`double`** 로 해석한다.  
`float`에 대입하려면 반드시 **`f` 또는 `F` 접미사**를 붙여야 한다.

```java
double d1 = 3.14;     // ✅ 기본이 double
double d2 = 314e-2;   // ✅

float f1 = 3.14;      // ❌ 컴파일 에러: double → float 손실 변환
float f2 = 3.14f;     // ✅ f 접미사로 float 명시
float f3 = 3E6F;      // ✅ F 접미사도 가능
```

### 정밀도 차이 — 코드로 확인

```java
float  var1 = 0.1234567890123456789f;
double var2 = 0.1234567890123456789;

System.out.println("float  : " + var1); // 0.12345679       ← 7자리까지만
System.out.println("double : " + var2); // 0.12345678901234568 ← 15자리까지
```

```
float  유효 자릿수: 0.1234567|9          (8번째 자리부터 반올림)
double 유효 자릿수: 0.123456789012345|68 (16번째 자리부터 반올림)
```

---

## 5️⃣ 실무에서 float vs double 선택 기준

| 상황 | 권장 타입 | 이유 |
|------|---------|------|
| 일반적인 계산 | `double` | Java 기본값, 정밀도 우수 |
| 메모리가 중요한 대용량 배열 | `float` | 메모리 절반 절약 |
| 금융/정밀 계산 | `BigDecimal` | 부동 소수점 오차 없음 |

### 💰 금융 계산에 float/double을 쓰면 안 되는 이유

```java
// 부동 소수점 오차 예시
System.out.println(0.1 + 0.2);  // 0.30000000000000004 ← 정확히 0.3이 아님!

// 금융 계산은 반드시 BigDecimal 사용
BigDecimal a = new BigDecimal("0.1");
BigDecimal b = new BigDecimal("0.2");
System.out.println(a.add(b));  // 0.3 ✅
```

> ⚠️ `float`과 `double`은 **이진 부동 소수점** 방식이라 일부 십진수를 정확히 표현하지 못한다.  
> 금액, 이자율 등 정확도가 중요한 계산에는 반드시 **`BigDecimal`** 을 사용해야 한다.

---

## 6️⃣ 핵심 정리

```
실수 저장 방식:  부호(1bit) + 지수(exponent) + 가수(mantissa)
float  = 32bit  → 유효 7자리,  범위 ±3.4×10³⁸
double = 64bit  → 유효 15자리, 범위 ±1.8×10³⁰⁸
```

| 개념 | 한 줄 요약 |
|------|-----------|
| **float** | 4byte, 유효 7자리, 리터럴에 `f/F` 접미사 필수 |
| **double** | 8byte, 유효 15자리, Java 실수 기본 타입 |
| **IEEE 754** | 부호 + 지수 + 가수로 실수를 비트에 저장하는 국제 표준 |
| **e 표기** | `5e2` = 500.0, `2e-3` = 0.002 |
| **정밀 계산** | 금융 등 오차 없는 계산은 `BigDecimal` 사용 |
