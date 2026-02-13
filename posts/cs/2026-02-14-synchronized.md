---
title: 🔒 Java synchronized — 한 번에 하나만 들어오세요
published: true
tags:
    - CS
    - 컴퓨터 구조
    - Programming
    - Java
    - Concurrency
    - Synchronized
    - Thread
    - Multithreading
    - JVM
    - ComputerScience

date: 2026-02-14
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 🔒 Java synchronized — 한 번에 하나만 들어오세요

> **한 줄 요약 :** synchronized는 객체의 Monitor Lock을 사용해 임계 영역을 한 번에 하나의 스레드만 실행하게 만들어 Data Race를 방지하는 동기화 메커니즘이다.

---

## ⚠️ 왜 필요한가 — Data Race

`count++` 한 줄이 실제로는 3단계 연산이다.

```
① count 읽기
② +1 계산
③ 다시 저장
```

멀티스레드 환경에서는 이 3단계 사이에 다른 스레드가 끼어든다.

```
Thread A : count 읽음 (0)
Thread B : count 읽음 (0)   ← A가 저장하기 전에 읽어버림
Thread A : 1 저장
Thread B : 1 저장           ← 2가 되어야 하는데 1이 됨
```

이것이 **Data Race**다. synchronized는 이 문제를 해결한다.

---

## ⚙️ 동작 원리 — Monitor Lock

Java의 모든 객체는 내부적으로 **Monitor Lock**을 하나씩 가진다.  
synchronized 블록에 진입하려면 반드시 이 락을 먼저 획득해야 한다.

```
Thread A → 락 획득 → 임계 영역 실행
Thread B → 락 대기 중 ...
Thread A → 작업 완료 → 락 반환
Thread B → 락 획득 → 임계 영역 실행
```

한 번에 하나의 스레드만 임계 영역(Critical Section)에 진입할 수 있다.

---

## ✅ synchronized가 해결하는 2가지 문제

### 1. 상호 배제 (Mutual Exclusion)

동시 접근을 막아 Data Race를 제거한다.

```java
public class Counter {
    private int count = 0;

    public synchronized void increment() {
        count++;  // 한 번에 하나의 스레드만 실행
    }
}
```

### 2. 메모리 가시성 (Memory Visibility)

synchronized 블록을 빠져나갈 때 변경된 값을 메인 메모리에 즉시 반영한다.  
다른 스레드가 캐시가 아닌 **최신 값**을 읽도록 보장한다.

---

## 🛠️ 사용 방식 3가지

```java
// 1. 메서드 전체 동기화
public synchronized void method() { ... }

// 2. 특정 블록만 동기화 (권장 — 임계 영역을 최소화)
public void method() {
    synchronized (this) {
        // 임계 영역
    }
}

// 3. 클래스 단위 동기화 (static 메서드)
public static synchronized void method() { ... }
```

> 💡 **블록 동기화를 권장하는 이유 :** 락을 잡고 있는 시간이 짧을수록 다른 스레드의 대기 시간이 줄어든다.

---

## 📌 핵심 요약

| 개념              | 설명                                          |
| ----------------- | --------------------------------------------- |
| **Monitor Lock**  | 모든 Java 객체가 가진 내부 락                 |
| **임계 영역**     | 한 번에 하나의 스레드만 실행 가능한 코드 구간 |
| **상호 배제**     | Data Race 제거                                |
| **메모리 가시성** | 캐시 불일치 문제 해결                         |
