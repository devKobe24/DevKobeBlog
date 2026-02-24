---
title: 🧩 JVM 메모리 사용 영역 (Runtime Data Area)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Stack
    - Heap
    - Method
    - Runtime Data Area

date: 2026-02-25
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🧩 JVM 메모리 사용 영역 (Runtime Data Area)

> "자바 코드가 실행될 때, JVM 안에서는 어떤 일이 벌어지는가?"

---

## 🔵 전체 구조 한눈에 보기

`java` 명령어로 JVM이 구동되면, OS로부터 할당받은 메모리를 **3가지 영역**으로 나눠 사용한다.

<img src="https://github.com/devKobe24/images2/blob/main/runtime-data-area-1.png?raw=true"/>

---

## 📚 메소드 영역 (Method Area)

**클래스 정보와 정적(static) 데이터가 저장되는 공간**

JVM이 `.class` 바이트코드 파일을 읽으면 그 내용이 이곳에 저장된다.
프로그램이 시작될 때 로드되어 **종료될 때까지 유지**된다.

| 저장 내용 | 설명 |
|-----------|------|
| **상수 (Constant)** | `final`로 선언된 상수 값 |
| **정적 필드 (Static Field)** | `static` 키워드로 선언된 클래스 변수 |
| **메소드 코드** | 메소드의 실제 바이트코드 명령어 |
| **생성자 코드** | 객체 생성 시 호출되는 생성자 코드 |

```java
public class Counter {
    static int count = 0;          // ← 메소드 영역 (정적 필드)
    static final int MAX = 100;    // ← 메소드 영역 (상수)

    public void increment() { }    // ← 메소드 영역 (메소드 코드)
}
```

> 💡 모든 스레드가 메소드 영역을 **공유**한다.
> `static` 변수에 여러 스레드가 동시에 접근하면 동기화 문제가 발생할 수 있는 이유가 여기에 있다.

---

## 🗃️ 힙 영역 (Heap Area)

**`new` 키워드로 생성된 객체와 배열이 저장되는 공간**

참조 타입 변수가 실제로 가리키는 데이터가 이곳에 존재한다.
메소드 영역과 스택 영역의 변수들이 힙 영역의 번지를 참조하는 방식으로 연결된다.

```java
String name = new String("강민성");
//            └─────── Heap에 객체 생성, name은 그 번지를 Stack에 저장
```

<img src="https://github.com/devKobe24/images2/blob/main/runtime-data-area-2.png?raw=true"/>

> 💡 힙 영역의 객체 중 **아무 변수도 참조하지 않는 객체**는 JVM의 **GC(Garbage Collector)** 가 자동으로 메모리를 회수한다.

---

## 🥞 스택 영역 (Stack Area)

**메소드 호출 시마다 프레임(Frame)이 쌓이고, 종료 시 자동 제거되는 공간**

스레드마다 **독립적인 Stack**을 가진다. (스레드 간 공유 ❌)

### 프레임(Frame)의 생존 주기

```
main() 호출     →  프레임-1 생성 (push)
 add() 호출     →  프레임-2 생성 (push)
 add() 종료     →  프레임-2 제거 (pop)
main() 종료     →  프레임-1 제거 (pop)
```

### 프레임 내부 저장 내용

| 저장 내용 | 예시 |
|-----------|------|
| **기본 타입 변수** | `int age = 25;` → Stack에 값 직접 저장 |
| **참조 타입 변수** | `String name = ...;` → Stack에 Heap 번지 저장 |
| **리턴 주소** | 메소드 종료 후 돌아갈 위치 |

```java
public static void main(String[] args) {         // 프레임-1 생성
    int age = 25;                                // Stack에 25 저장
    String name = new String("강민성");          // Stack에 Heap 번지 저장
    printInfo(age, name);                        // 프레임-2 생성
}                                                // 프레임-1 제거

static void printInfo(int a, String n) {        // 프레임-2
    System.out.println(a + " " + n);
}                                               // 프레임-2 제거
```

> ⚠️ Stack은 크기가 제한되어 있어, 재귀 호출이 너무 깊어지면 **StackOverflowError** 가 발생한다.

---

## ⚖️ 3가지 영역 최종 비교

| 항목 | 메소드 영역 | 힙 영역 | 스택 영역 |
|------|------------|---------|----------|
| 저장 내용 | 클래스 정보, static, 상수 | 객체, 배열 | 지역 변수, 프레임 |
| 생존 기간 | 프로그램 전체 | GC가 수거할 때까지 | 메소드 실행 중 |
| 스레드 공유 | ✅ 공유 | ✅ 공유 | ❌ 스레드 독립 |
| 관련 오류 | — | `OutOfMemoryError` | `StackOverflowError` |

---

## 📝 핵심 요약

**메소드 영역**은 클래스 코드와 `static` 데이터를 저장하며 프로그램 내내 유지된다.
**힙 영역**은 `new`로 생성된 객체가 살아있는 공간으로 GC가 관리한다.
**스택 영역**은 메소드 호출마다 프레임이 쌓이고 종료 시 자동 제거되며 스레드마다 독립적으로 존재한다.
