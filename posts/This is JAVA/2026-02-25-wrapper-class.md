---
title: 🎁 포장 클래스 (Wrapper Class)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Wrapper Class
    - Primitive Type
    - Boxing
    - Unboxing
    - Auto Boxing
    - Auto Unboxing

date: 2026-02-25
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🎁 포장 클래스 (Wrapper Class)

> "기본 타입은 객체가 아니다. 객체가 필요한 순간, 포장 클래스가 등장한다."

---

## 🔵 포장 클래스란?

자바의 기본 타입(`int`, `double` 등)은 **객체가 아니다.**
그런데 컬렉션(`List`, `Map` 등)처럼 **객체만 다룰 수 있는 곳**에서는 기본 타입을 직접 쓸 수 없다.
이때 기본 타입의 값을 객체로 감싸는 역할을 하는 것이 **포장 클래스(Wrapper Class)** 다.

<img src="https://github.com/devKobe24/images2/blob/main/wrapper-class-1.png?raw=true"/>

---

## 🗂️ 기본 타입 → 포장 클래스 대응표

| 기본 타입 | 포장 클래스 | 특이사항 |
|-----------|------------|---------|
| `byte` | `Byte` | |
| `char` | `Character` | ⚠️ 이름 완전히 다름 |
| `short` | `Short` | |
| `int` | `Integer` | ⚠️ 이름 다름 |
| `long` | `Long` | |
| `float` | `Float` | |
| `double` | `Double` | |
| `boolean` | `Boolean` | |

> 💡 `char` → `Character`, `int` → `Integer` 두 가지만 이름이 다르고,
> 나머지는 기본 타입 첫 글자를 대문자로 바꾼 이름이다.

---

## 🔄 박싱(Boxing) & 언박싱(Unboxing)

### 박싱 — 기본 타입 → 포장 객체

```java
Integer obj = 100;    // int → Integer 로 자동 Boxing
Double  d   = 3.14;   // double → Double 로 자동 Boxing
```

### 언박싱 — 포장 객체 → 기본 타입

```java
int value = obj;          // Integer → int 로 자동 Unboxing
int result = obj + 50;    // 연산 전 자동 Unboxing 후 계산
```

```
박싱 (Boxing)
int  100  ───────────────►  Integer 객체 (Heap)
                              └─ 값: 100

언박싱 (Unboxing)
Integer 객체 (Heap) ────────►  int  100
```

> 💡 박싱/언박싱은 자바가 **자동(Auto)** 으로 처리해준다.
> 이를 **오토박싱(Auto-boxing) / 오토언박싱(Auto-unboxing)** 이라고 한다.

### 📌 종합 예제

```java
public class BoxingUnBoxingExample {
    public static void main(String[] args) {
        // Boxing: int → Integer
        Integer obj = 100;
        System.out.println("value: " + obj.intValue());  // 100

        // Unboxing: Integer → int
        int value = obj;
        System.out.println("value: " + value);           // 100

        // 연산 시 자동 Unboxing
        int result = obj + 100;
        System.out.println("result: " + result);         // 200
    }
}
```

```
실행 결과

value: 100
value: 100
result: 200
```

---

## 🔤 문자열 → 기본 타입 변환

포장 클래스의 `parse타입()` 정적 메소드를 사용하면 **문자열을 기본 타입으로 변환**할 수 있다.

| 변환 | 메소드 | 예시 |
|------|--------|------|
| `String` → `int` | `Integer.parseInt()` | `Integer.parseInt("100")` → `100` |
| `String` → `double` | `Double.parseDouble()` | `Double.parseDouble("3.14")` → `3.14` |
| `String` → `boolean` | `Boolean.parseBoolean()` | `Boolean.parseBoolean("true")` → `true` |
| `String` → `long` | `Long.parseLong()` | `Long.parseLong("5000")` → `5000L` |

```java
String ageStr   = "25";
String priceStr = "99.9";
String flagStr  = "true";

int     age   = Integer.parseInt(ageStr);        // 25
double  price = Double.parseDouble(priceStr);    // 99.9
boolean flag  = Boolean.parseBoolean(flagStr);   // true
```

> 💡 API 응답, 사용자 입력 등 **외부에서 데이터를 받을 때** 항상 String으로 들어오므로,
> 실무에서 `parseInt()` 계열 메소드를 매우 자주 사용한다.

---

## ⚖️ 포장 객체 값 비교 — `==` 함정 주의

포장 객체는 참조 타입이므로 `==`는 **번지(주소)를 비교**한다.
내부 값이 같아도 다른 객체라면 `false`가 나올 수 있다.

```java
Integer obj1 = 300;
Integer obj2 = 300;
System.out.println(obj1 == obj2);      // ❌ false (다른 객체)
System.out.println(obj1.equals(obj2)); // ✅ true  (값 비교)
```

### ⚠️ 예외 — JVM 캐싱 범위

성능 최적화를 위해 JVM은 특정 범위의 포장 객체를 **재사용(캐싱)** 한다.
이 범위 안에서는 `==`가 `true`가 될 수 있다.

| 타입 | 캐싱 범위 |
|------|-----------|
| `Boolean` | `true`, `false` |
| `Character` | `\u0000` ~ `\u007F` (0~127) |
| `Byte`, `Short`, `Integer` | `-128` ~ `127` |

```java
Integer a = 10;   // 캐싱 범위 내
Integer b = 10;
System.out.println(a == b);      // true  (같은 캐시 객체)
System.out.println(a.equals(b)); // true

Integer c = 300;  // 캐싱 범위 초과
Integer d = 300;
System.out.println(c == d);      // false (새로 생성된 다른 객체)
System.out.println(c.equals(d)); // true
```

> ⚠️ **결론: 포장 객체 값 비교는 항상 `equals()`를 사용하라.**
> `==`는 캐싱 범위에 따라 결과가 달라지는 **예측 불가능한 동작**을 일으킨다.

---

## 🧩 포장 클래스를 사용하는 이유

```java
// ❌ 기본 타입은 컬렉션에 직접 저장 불가
List<int> list = new ArrayList<>();       // 컴파일 에러

// ✅ 포장 클래스를 사용해야 함
List<Integer> list = new ArrayList<>();
list.add(1);    // 자동 Boxing
list.add(2);
int val = list.get(0);  // 자동 Unboxing
```

| 사용 이유 | 설명 |
|-----------|------|
| 컬렉션 저장 | `List<Integer>`, `Map<String, Double>` 등 |
| `null` 표현 | 기본 타입은 `null` 불가, 포장 클래스는 가능 |
| 문자열 변환 | `parseInt()`, `parseDouble()` 등 유틸 메소드 |
| 제네릭 타입 | `<T>` 타입 파라미터에는 객체만 사용 가능 |

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| 포장 클래스 | 기본 타입을 객체로 감싸는 클래스 (`java.lang` 패키지) |
| 주의 이름 | `char` → `Character`, `int` → `Integer` |
| 박싱 | 기본 타입 → 포장 객체 (자동) |
| 언박싱 | 포장 객체 → 기본 타입 (자동, 연산 시에도 발생) |
| 문자열 변환 | `Integer.parseInt()`, `Double.parseDouble()` 등 |
| 값 비교 | `==` ❌ → **`equals()`** ✅ (캐싱 예외 있으나 항상 equals 권장) |
