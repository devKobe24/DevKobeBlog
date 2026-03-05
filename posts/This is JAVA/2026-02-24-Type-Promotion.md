---
title: 🔄 자동 타입 변환 (Promotion)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Data Type
    - Type Promotion
    - Primitive Types

date: 2026-02-23
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔄 자동 타입 변환 (Promotion)

> "작은 그릇의 물은 큰 그릇에 그대로 담긴다. 반대는 넘친다."

---

## 🔵 자동 타입 변환이란?

<img src="https://github.com/devKobe24/images2/blob/main/promotion-1.png?raw=true"/>

**자동 타입 변환(Promotion)** = 허용 범위가 **작은 타입 → 큰 타입**으로 대입 시 자동으로 변환되는 것

개발자가 명시적으로 캐스팅하지 않아도 컴파일러가 알아서 처리해준다.

### 📐 기본 타입의 허용 범위 순서

```
byte(1) < short(2) · char(2) < int(4) < long(8) < float(4) < double(8)
                                                   ↑
                                         (크기는 작지만 범위가 더 큼)
```

> 💡 `float`은 4byte지만 소수점 표현 방식(부동소수점) 덕분에 `long`(8byte)보다 **더 넓은 범위**를 표현할 수 있다.

---

## 🧱 메모리에서 벌어지는 일

`byte`(1byte) → `int`(4byte)로 변환할 때, 나머지 3byte는 **0으로 채워진다 (Zero Padding)**.

<img src="https://github.com/devKobe24/images2/blob/main/promotion-2.png?raw=true"/>

값 자체는 그대로 유지되고, 더 넓은 공간에 안전하게 옮겨진다.

```java
byte byteValue = 10;
int intValue = byteValue;  // 자동 변환 → intValue = 10
```

---

## ✅ 자동 변환이 되는 경우

### 1. 정수 → 정수

```java
byte  b = 10;
int   i = b;      // ✅ byte → int
long  l = i;      // ✅ int  → long
```

### 2. 정수 → 실수 (항상 허용)

정수 타입은 무조건 실수 타입으로 자동 변환된다.

```java
long   longValue   = 5_000_000_000L;
float  floatValue  = longValue;   // ✅ 5.0E9f
double doubleValue = longValue;   // ✅ 5.0E9
```

### 3. char → int (유니코드 값으로 변환)

`char`를 `int`에 대입하면 해당 문자의 **유니코드(정수) 값**이 저장된다.

```java
char charValue = 'A';
int  intValue  = charValue;  // ✅ 65 저장

char ga = '가';
int  gaCode = ga;            // ✅ 44032 저장
```

---

## ❌ 자동 변환이 안 되는 경우 — byte → char

`byte`는 크기상 `char`보다 작지만, **음수를 포함**하기 때문에 자동 변환이 불가능하다.

| 타입 | 범위 |
|------|------|
| `byte` | -128 ~ 127 (**음수 포함**) |
| `char` | 0 ~ 65,535 (**음수 없음**) |

```java
byte byteValue = 65;
char charValue = byteValue;  // ❌ 컴파일 에러

// ✅ 명시적 캐스팅으로 해결
char charValue = (char) byteValue;  // 'A'
```

---

## 🧪 종합 예제

```java
public class PromotionExample {
    public static void main(String[] args) {
        // 1. byte → int
        byte byteValue = 10;
        int intValue = byteValue;
        System.out.println("intValue: " + intValue);          // 10

        // 2. char → int (유니코드)
        char charValue = '가';
        intValue = charValue;
        System.out.println("가의 유니코드: " + intValue);      // 44032

        // 3. int → long
        intValue = 50;
        long longValue = intValue;
        System.out.println("longValue: " + longValue);         // 50

        // 4. long → float
        longValue = 100;
        float floatValue = longValue;
        System.out.println("floatValue: " + floatValue);       // 100.0

        // 5. float → double
        floatValue = 100.5F;
        double doubleValue = floatValue;
        System.out.println("doubleValue: " + doubleValue);     // 100.5
    }
}
```

```
실행 결과

intValue: 10
가의 유니코드: 44032
longValue: 50
floatValue: 100.0
doubleValue: 100.5
```

---

## ⚡ 연산 시 자동 타입 변환

변수 대입 외에도 **산술 연산** 시 자동 변환이 발생한다.

```java
byte a = 10;
byte b = 20;
// byte result = a + b;  ❌ 컴파일 에러!
int  result = a + b;     // ✅ 연산 결과는 int로 자동 변환
```

> 💡 `byte`, `short`, `char` 타입끼리의 연산 결과는 **항상 `int`** 로 변환된다.
> 연산 중 오버플로우를 방지하기 위한 자바의 안전장치다.

```java
int  i = 10;
long l = 20L;
// int result = i + l;   ❌ 컴파일 에러
long result = i + l;     // ✅ int + long → long 으로 자동 변환

int  n = 10;
double d = 3.14;
// int result2 = n + d;  ❌ 컴파일 에러
double result2 = n + d;  // ✅ int + double → double 로 자동 변환
```

**규칙 요약:** 두 타입이 다를 경우 **더 큰 타입으로 통일** 후 연산한다.

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| 자동 변환 조건 | 작은 타입 → 큰 타입 대입 시 자동 발생 |
| 허용 범위 순서 | `byte` < `short`·`char` < `int` < `long` < `float` < `double` |
| 메모리 처리 | 나머지 공간은 0으로 채워짐 (Zero Padding) |
| 정수 → 실수 | 항상 자동 변환 허용 |
| `char` → `int` | 유니코드 정수값으로 변환 |
| `byte` → `char` | ❌ 음수 범위 불일치로 불가 (명시적 캐스팅 필요) |
| 연산 시 변환 | `byte`/`short`/`char` 연산 결과는 항상 `int` |
