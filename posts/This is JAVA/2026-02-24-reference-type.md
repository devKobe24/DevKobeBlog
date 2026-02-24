---
title: 📦 참조 타입 (Reference Type)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Data Type
    - Referece Type
    - Primitive Type
    - Stack
    - Heap

date: 2026-02-24
thumbnail: /assets/img/thumbnail/java.jpg
---

# 📦 참조 타입 (Reference Type)

> "기본 타입은 값 그 자체, 참조 타입은 값이 있는 곳의 주소"

---

## 🗂️ 자바 데이터 타입 전체 분류

<img src="https://github.com/devKobe24/images2/blob/main/reference-type-1.png?raw=true"/>

> 💡 **객체(Object)란?**
> 데이터(필드) + 메소드가 결합된 덩어리.
> 참조 타입 변수는 이 객체가 실제로 존재하는 **메모리 주소(번지)** 를 저장한다.

---

## 🔑 핵심 차이 — 무엇을 저장하는가?

| 구분 | 저장하는 것 | 메모리 위치 |
|------|-------------|-------------|
| **기본 타입** | 값 자체 (`25`, `100.5`) | Stack |
| **참조 타입** | 객체의 메모리 번지 (`100번지`) | Stack → Heap 참조 |

<img src="https://github.com/devKobe24/images2/blob/main/reference-type-2.png?raw=true"/>

```
 기본 타입 변수             참조 타입 변수
┌───────────┐          ┌───────────┐         ┌───────────────┐
│   값 자체   │          │  100번지   │ ──────► │  실제 객체      │
└───────────┘          └───────────┘         └───────────────┘
  Stack 직접 저장        Stack에 주소 저장            Heap에 존재
```

---

## 🧠 메모리 구조로 이해하기

아래 코드가 메모리에서 어떻게 동작하는지 살펴보자.

```java
// 기본 타입
int    age   = 25;
double price = 100.5;

// 참조 타입
String name  = "강민성";
String hobby = "독서";
```

<img src="https://github.com/devKobe24/images2/blob/main/reference-type-3.png?raw=true"/>

- `age`, `price` → Stack에 **값 자체** 저장
- `name`, `hobby` → Stack에 **Heap 주소** 저장, 실제 문자열 객체는 Heap에 위치

---

## 🔍 참조 타입의 특징

### 1. `null` 할당 가능

참조 타입 변수는 아직 객체를 가리키지 않을 때 `null`을 저장할 수 있다.
기본 타입은 `null`을 가질 수 없다.

```java
String name = null;   // ✅ 참조 타입은 null 가능
int    age  = null;   // ❌ 컴파일 에러 — 기본 타입은 null 불가
```

> ⚠️ `null` 상태의 참조 변수로 메서드를 호출하면 **NullPointerException(NPE)** 이 발생한다.
> ```java
> String name = null;
> int len = name.length();  // 💥 NullPointerException!
> ```

### 2. `==` 연산은 번지 비교

참조 타입 변수끼리 `==`로 비교하면 **값이 같은지가 아니라 같은 객체를 가리키는지** 비교한다.

```java
String a = new String("hello");
String b = new String("hello");

System.out.println(a == b);       // false (서로 다른 Heap 주소)
System.out.println(a.equals(b));  // true  (값이 같음)
```

### 3. GC(Garbage Collector)의 대상

Heap에 생성된 객체가 더 이상 아무 변수에도 참조되지 않으면 JVM의 GC가 자동으로 메모리를 회수한다.

```java
String name = "강민성";  // Heap에 "강민성" 객체 생성
name = null;             // 참조 해제 → GC 대상이 됨
```

---

## ⚖️ 기본 타입 vs 참조 타입 최종 비교

| 항목 | 기본 타입 (Primitive) | 참조 타입 (Reference) |
|------|----------------------|----------------------|
| 저장 내용 | 값 자체 | 객체의 메모리 번지 |
| 메모리 위치 | Stack | Stack(번지) + Heap(객체) |
| `null` 가능 여부 | ❌ 불가 | ✅ 가능 |
| `==` 비교 | 값 비교 | 번지(주소) 비교 |
| 종류 | byte, short, int, long, float, double, char, boolean | 배열, 클래스, 인터페이스, 열거형 |
| 기본값 | `0`, `0.0`, `false` 등 | `null` |

---

## 📝 핵심 요약

기본 타입은 Stack에 **값 자체**를 저장하고, 참조 타입은 Stack에 **Heap의 주소**를 저장한다.
참조 타입의 실제 객체는 Heap에 존재하며, JVM의 GC가 사용되지 않는 객체를 자동 정리한다.
참조 타입을 다룰 때는 **NPE 방지**와 **값 비교 시 `equals()` 사용**을 항상 염두에 두자.
