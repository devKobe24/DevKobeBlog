---
title: 📦 JVM 메모리 — Stack과 Heap, 뭐가 어디 저장될까?
published: true
tags:
    - CS
    - 컴퓨터 구조
    - Programming
    - JVM
    - ComputerScience
    - Memory
    - Stack
    - Heap
    - GarbageCollection

date: 2026-02-14
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 📦 JVM 메모리 — Stack과 Heap, 뭐가 어디 저장될까?

> **한 줄 요약 :** Stack은 메서드 실행을 위한 공간이고, Heap은 객체가 실제로 존재하는 공간이다. Stack은 객체를 저장하지 않고 **주소(참조값)만** 저장한다.

---

## 🗺️ 전체 구조 한눈에 보기

```
┌─────────────────────────────────┐
│           Heap (공유)            │  ← new로 생성된 객체 저장
│         모든 스레드가 공유           │     GC가 관리
└─────────────────────────────────┘

┌──────────────┐  ┌──────────────┐
│   Stack (A)  │  │   Stack (B)  │  ← 스레드마다 독립적으로 존재
│ Thread A 전용 │  │ Thread B 전용 │     메서드 호출·지역변수 저장
└──────────────┘  └──────────────┘
```

---

## 1️⃣ Stack — 메서드 호출의 무대

### 특징

| 항목 | 내용 |
|------|------|
| **구조** | LIFO (Last In First Out) |
| **범위** | 스레드마다 독립적으로 존재 |
| **속도** | 빠름 |
| **저장 대상** | 지역변수, 참조값, 호출 정보 |

### 동작 흐름

```java
public static void main(String[] args) {
    int a = 10;
    method1();
}

static void method1() {
    int b = 20;
}
```

```
① main() 호출     → [main  Frame : a=10        ] 생성
② method1() 호출  → [method1 Frame : b=20       ] 쌓임
③ method1() 종료  → [method1 Frame              ] 제거
④ main() 종료     → [main  Frame                ] 제거
```

메서드가 호출될 때마다 **Stack Frame**이 쌓이고, 종료되면 즉시 사라진다.

---

## 2️⃣ Heap — 객체가 실제로 사는 곳

### 특징

| 항목 | 내용 |
|------|------|
| **범위** | 모든 스레드가 공유 |
| **생성 시점** | `new` 키워드 사용 시 |
| **삭제 시점** | 참조가 끊기면 GC가 수거 |
| **속도** | Stack보다 상대적으로 느림 |

### 동작 흐름

```java
User user = new User();  // Heap에 객체 생성, Stack에 주소 저장
user = null;             // 참조 해제 → GC 수거 대상
```

---

## 3️⃣ Stack과 Heap이 함께 동작하는 방식

```java
User user = new User();
```

```
[ Stack ]                    [ Heap ]
user (참조변수)  ──────────▶  User 객체
= 0x1000                     0x1000 : name = "Kobe"
                                       age  = 30
```

> ⚠️ Stack에는 객체 자체가 아닌 **Heap의 주소(참조값)** 만 저장된다.

### 클래스 필드가 객체를 참조하는 경우

```java
class A {
    User user;
}
```

```
[ Heap ]
A 객체
 └── user 필드 (참조값) ──▶ User 객체
```

인스턴스 변수도, 그 안의 참조값도, 실제 객체도 **모두 Heap에 존재**한다.

---

## 📌 Stack vs Heap 비교

| 구분 | Stack | Heap |
|------|-------|------|
| **저장 대상** | 지역변수, 참조값 | 객체 |
| **생성 시점** | 메서드 호출 | `new` 키워드 |
| **삭제 시점** | 메서드 종료 | GC 수거 |
| **속도** | 빠름 | 상대적으로 느림 |
| **스레드** | 독립 (스레드별) | 공유 (전체) |
