---
title: 🔧 강제 타입 변환 (Casting)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Data Type
    - Type Casting

date: 2026-02-24
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔧 강제 타입 변환 (Casting)

> "큰 그릇을 작은 그릇에 넣으려면, 쪼개서 필요한 부분만 담아야 한다."

---

## 🔵 강제 타입 변환이란?

**강제 타입 변환(Casting)** = 큰 허용 범위 타입 → 작은 허용 범위 타입으로 **개발자가 직접 지정**해서 변환

<img src="https://github.com/devKobe24/images2/blob/main/type-casting-1.png?raw=true"/>

자동 타입 변환(Promotion)과 반대 방향이다.
자바는 데이터 손실을 막기 위해 자동으로 해주지 않으므로, 캐스팅 연산자 `(타입)`을 명시해야 한다.

```java
// 문법
작은타입 변수 = (작은타입) 큰타입변수;
```

---

## 🧱 메모리에서 벌어지는 일

### ✅ 값이 보존되는 경우 — `int(10)` → `byte`

`int` 10은 2진수로 `00001010`, 1byte 안에 충분히 표현 가능하다.
캐스팅 시 앞 3byte는 **버려지고** 끝 1byte만 `byte`에 저장된다.

<img src="https://github.com/devKobe24/images2/blob/main/type-casting-2.png?raw=true"/>

```java
int  intValue  = 10;
byte byteValue = (byte) intValue;  // ✅ byteValue = 10 (보존)
```

---

### ❌ 값이 손실되는 경우 — `int(103029770)` → `byte`

`int` 103029770은 4byte 전체를 사용하는 큰 값이다.
캐스팅 시 앞 3byte가 버려지면서 **전혀 다른 값**이 저장된다.

<img src="https://github.com/devKobe24/images2/blob/main/type-casting-3.png?raw=true"/>

```java
int  intValue  = 103029770;
byte byteValue = (byte) intValue;  // ❌ byteValue = 10 (원래 값 손실!)
```

> ⚠️ **핵심 원칙**: 캐스팅은 **대상 타입의 허용 범위 안에 있는 값**에만 안전하다.
> `byte`로 캐스팅하려면 값이 **-128 ~ 127** 사이여야 원래 값이 보존된다.

---

## 🔄 주요 캐스팅 케이스

### 1. `int` → `byte`

```java
int  intValue  = 10;
byte byteValue = (byte) intValue;  // ✅ 10 보존 (-128~127 범위 내)

int  intValue2  = 200;
byte byteValue2 = (byte) intValue2;  // ❌ -56 저장 (범위 초과 → 데이터 손실)
```

### 2. `long` → `int`

`long`(8byte) → `int`(4byte) 변환 시 앞 4byte 버려지고 끝 4byte만 저장된다.
`int` 허용 범위(`약 ±21억`) 안의 값이라면 안전하게 보존된다.

```java
long longValue = 300L;
int  intValue  = (int) longValue;  // ✅ 300 보존
```

### 3. `int` → `char`

`int` 값을 유니코드로 해석해 해당 문자로 변환한다.
`char` 허용 범위(`0 ~ 65535`) 안의 값만 안전하다.

```java
int  intValue  = 65;
char charValue = (char) intValue;
System.out.println(charValue);  // ✅ 'A' 출력
```

> 💡 반대로 `char` → `int`는 **자동 변환**이 되어 유니코드 숫자가 저장된다.
> 캐스팅 없이 `int intValue = charValue;` 로 사용 가능하다.

### 4. `실수` → `정수` (소수점 손실 주의)

소수점 이하는 **버림 처리**된다. 반올림이 아님에 주의.

```java
double doubleValue = 3.99;
int    intValue    = (int) doubleValue;  // ✅ 3 저장 (3.99 → 3, 버림)

double d2 = -3.99;
int    i2  = (int) d2;  // -3 저장 (0 방향으로 버림)
```

> ⚠️ 반올림이 필요하다면 `Math.round()` 를 사용해야 한다.
> ```java
> long rounded = Math.round(3.99);  // 4
> ```

---

## 🧪 종합 예제

```java
public class CastingExample {
    public static void main(String[] args) {
        // 1. int → byte (범위 내 → 보존)
        int  var1 = 10;
        byte var2 = (byte) var1;
        System.out.println(var2);   // 10

        // 2. long → int (범위 내 → 보존)
        long var3 = 300L;
        int  var4 = (int) var3;
        System.out.println(var4);   // 300

        // 3. int → char (유니코드로 해석)
        int  var5 = 65;
        char var6 = (char) var5;
        System.out.println(var6);   // A

        // 4. double → int (소수점 버림)
        double var7 = 3.14;
        int    var8 = (int) var7;
        System.out.println(var8);   // 3
    }
}
```

```
실행 결과

10
300
A
3
```

---

## 🛡️ 오버플로우 방지 — 캐스팅 전 범위 확인

실무에서 `int` 값을 `byte`로 캐스팅하기 전에 안전한지 확인하는 패턴이다.

```java
public static void castSafely(int value) {
    if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
        byte b = (byte) value;
        System.out.println("안전 변환: " + b);
    } else {
        System.out.println("범위 초과! 캐스팅 불가: " + value);
    }
}

castSafely(100);        // 안전 변환: 100
castSafely(200);        // 범위 초과! 캐스팅 불가: 200
```

> 💡 자바는 `Byte.MIN_VALUE(-128)`, `Byte.MAX_VALUE(127)`, `Integer.MAX_VALUE` 등 **유용한 범위 상수**를 제공한다.

---

## 📝 핵심 요약

| 변환 | 방법 | 주의 사항 |
|------|------|-----------|
| `int` → `byte` | `(byte) intValue` | -128 ~ 127 범위 내 값만 보존 |
| `long` → `int` | `(int) longValue` | 약 ±21억 범위 내 값만 보존 |
| `int` → `char` | `(char) intValue` | 0 ~ 65535 범위 내 값만 보존, 유니코드 문자로 해석 |
| `double` → `int` | `(int) doubleValue` | 소수점 이하 **버림** (반올림 아님) |
| 값 보존 원칙 | — | 대상 타입 허용 범위 안의 값이어야 데이터 손실 없음 |
| 반올림 필요 시 | `Math.round()` | 캐스팅 전 반올림 후 변환 |
