---
title: 📦 데이터는 메모리 어디에 저장될까?
published: true
tags:
    - cs
    - 컴퓨터 구조
    - Programming

date: 2026-02-21
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 📦 데이터는 메모리 어디에 저장될까?

### — Stack과 Heap 완전 정복

> **핵심 요약:** 모든 데이터가 같은 메모리 공간에 저장되지 않는다.  
> **Stack**은 함수 실행을 위한 짧은 수명 데이터, **Heap**은 동적으로 생성되는 긴 수명 데이터를 담는다.

---

## 1️⃣ Stack — 함수 실행을 위한 공간

함수가 호출될 때 생성되고, 함수가 끝나면 **자동으로 사라지는** 데이터들이 저장된다.

### Stack에 저장되는 것

<img src="https://github.com/devKobe24/images2/blob/main/stack-and-heap-1.png?raw=true"/>

```java
void calculate(int a, int b) {  // a, b → Stack
    int result = a + b;          // result → Stack
    return result;
}   // ← 메서드 종료 시 Stack Frame 전체 자동 해제
```

### Stack의 특징

| 특징          | 설명                                                         |
| ------------- | ------------------------------------------------------------ |
| **자동 관리** | 함수 종료 시 자동 해제, 개발자가 신경 쓸 필요 없음           |
| **매우 빠름** | 연속된 메모리 구조, push/pop 단순 연산                       |
| **크기 제한** | 기본 수 MB 수준, 깊은 재귀 호출 시 `StackOverflowError` 발생 |
| **LIFO 구조** | 가장 최근에 호출된 함수가 가장 먼저 종료                     |

### 함수 호출 스택 구조

<img src="https://github.com/devKobe24/images2/blob/main/stack-and-heap-2.png?raw=true"/>

---

## 2️⃣ Heap — 동적으로 생성되는 데이터 공간

런타임 중 **`new` 키워드로 생성**되는 데이터들이 저장된다.  
함수가 끝나도 **참조가 살아있는 한 데이터는 유지**된다.

### Heap에 저장되는 것

```java
Person p = new Person("홍길동", 30);
//  ↑ Stack           ↑ Heap
// (참조값 p)       (실제 객체 데이터)

int[] arr = new int[100];  // 배열도 Heap
List<String> list = new ArrayList<>();  // 컬렉션도 Heap
```

### Heap의 특징

| 특징                | 설명                                     |
| ------------------- | ---------------------------------------- |
| **유연한 수명**     | 참조가 사라질 때까지 유지                |
| **상대적으로 느림** | 할당/해제 관리 비용, 메모리 단편화 가능  |
| **큰 용량**         | 수십 GB까지 설정 가능 (`-Xmx` 옵션)      |
| **GC가 관리**       | Java에서는 Garbage Collector가 자동 정리 |

---

## 3️⃣ Stack vs Heap — 한눈에 비교

<img src="https://github.com/devKobe24/images2/blob/main/stack-and-heap-3.png?raw=true"/>

| 구분            | Stack                        | Heap                       |
| --------------- | ---------------------------- | -------------------------- |
| **저장 데이터** | 지역변수, 매개변수, 참조변수 | 객체, 배열, 컬렉션         |
| **수명**        | 함수 스코프 내               | 참조가 유지되는 동안       |
| **관리 주체**   | 컴파일러 / 런타임 자동       | GC (Java) / 개발자 (C/C++) |
| **속도**        | 매우 빠름                    | 상대적으로 느림            |
| **크기**        | 수 MB (제한적)               | 수 GB (유연)               |
| **오류**        | `StackOverflowError`         | `OutOfMemoryError`         |

---

## 4️⃣ Java에서 헷갈리는 포인트 정리

### 📌 참조 변수와 객체는 다른 위치에 있다

```java
Person p = new Person("홍길동");
```

<img src="https://github.com/devKobe24/images2/blob/main/stack-and-heap-4.png?raw=true"/>

- `p` 변수 자체는 **Stack**에 저장 (주소값 보관)
- `Person` 객체의 실제 데이터는 **Heap**에 저장

### 📌 원시 타입 vs 참조 타입

```java
int age = 30;           // age의 값(30) 자체가 Stack에 저장
Person p = new Person();  // p는 Stack, Person 객체는 Heap에 저장
```

| 구분                      | 타입      | Stack             | Heap           |
| ------------------------- | --------- | ----------------- | -------------- |
| `int age = 30`            | 원시 타입 | 값(30) 직접 저장  | —              |
| `Person p = new Person()` | 참조 타입 | 주소값(0xA1) 저장 | 실제 객체 저장 |

### ⚠️ StackOverflowError vs OutOfMemoryError

```java
// StackOverflowError — 재귀가 너무 깊어 Stack 공간 초과
void infinite() {
    infinite();  // 종료 조건 없는 재귀 → StackOverflowError
}

// OutOfMemoryError — Heap 공간 초과
List<byte[]> leak = new ArrayList<>();
while (true) {
    leak.add(new byte[1024 * 1024]);  // 계속 Heap 점유 → OOM
}
```

---

## 5️⃣ GC(Garbage Collector)와 Heap

Java에서 Heap의 객체는 **GC가 자동으로 정리**한다.

```java
void method() {
    Person p = new Person("홍길동");  // Heap에 객체 생성
    // ... 작업 수행
}
// method() 종료 → p(Stack)가 사라짐
// → Person 객체를 가리키는 참조가 없어짐
// → GC가 다음 실행 시 Heap에서 Person 객체 제거 ✅
```

```
참조 있음:  Stack[p: 0xA1] ──▶ Heap[Person 객체]  → 유지
참조 없음:  (참조 변수 사라짐)   Heap[Person 객체]  → GC 수거 대상
```

---

## 6️⃣ 핵심 정리

```
함수 호출  →  Stack Frame 생성  →  지역변수/매개변수 저장
new 키워드 →  Heap에 객체 생성  →  참조 변수가 Stack에서 가리킴
함수 종료  →  Stack Frame 자동 제거
참조 소멸  →  GC가 Heap 객체 수거
```

| 개념                   | 한 줄 요약                                         |
| ---------------------- | -------------------------------------------------- |
| **Stack**              | 함수 호출마다 생기는 자동 관리 메모리, 빠르고 작음 |
| **Heap**               | `new`로 생성되는 동적 메모리, 유연하고 GC가 관리   |
| **참조 변수**          | Stack에 저장되며, Heap의 객체 주소를 가리킴        |
| **StackOverflowError** | Stack 공간 초과 (깊은 재귀 등)                     |
| **OutOfMemoryError**   | Heap 공간 초과 (메모리 누수 등)                    |
| **GC**                 | Heap에서 참조 없는 객체를 자동으로 수거            |
