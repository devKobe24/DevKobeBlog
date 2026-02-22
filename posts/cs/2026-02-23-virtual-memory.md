---
title: 🌐 RAM이 8GB뿐인데 16GB 프로그램을 실행할 수 있을까?
published: true
tags:
    - cs
    - 컴퓨터 구조
    - Programming

date: 2026-02-23
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 🌐 RAM이 8GB뿐인데 16GB 프로그램을 실행할 수 있을까?
### — 가상 메모리(Virtual Memory) 완전 정복

> **핵심 요약:** 프로세스에게 "RAM을 통째로 혼자 쓰고 있다"는 환상을 심어준다.  
> 실제로는 RAM과 디스크를 조합해 관리하지만, 프로그램은 하나의 거대한 연속 메모리처럼 인식한다.

---

## 1️⃣ 가상 메모리란?

물리적인 RAM 크기와 무관하게, 각 프로세스에게 **독립적이고 거대한 메모리 공간**을 제공하는 OS의 메모리 관리 기법이다.

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-1.png?raw=true"/>

---

## 2️⃣ 핵심 메커니즘 — 주소 변환

### 논리 주소 vs 물리 주소

| 구분 | 논리(가상) 주소 | 물리 주소 |
|------|---------------|---------|
| 사용 주체 | CPU, 프로세스 | RAM 하드웨어 |
| 시작 번지 | 프로세스마다 0번지부터 | 실제 RAM 위치 |
| 연속성 | 프로그램에겐 연속적으로 보임 | 실제로는 흩어져 있을 수 있음 |
| 변환 | MMU가 실시간 변환 | — |

### MMU(Memory Management Unit)

CPU가 논리 주소로 메모리를 요청하면 **MMU**가 실시간으로 물리 주소로 변환한다.

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-2.png?raw=true"/>

---

## 3️⃣ 페이징(Paging) — 가상 메모리 관리 방식

가상 메모리를 관리하는 가장 보편적인 방법이다.

### 핵심 개념 3가지

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-3.png?raw=true"/>

| 용어 | 의미 |
|------|------|
| **페이지 (Page)** | 가상 메모리를 일정 크기로 나눈 블록 (보통 4KB) |
| **프레임 (Frame)** | 물리 RAM을 페이지와 같은 크기로 나눈 블록 |
| **페이지 테이블** | 어떤 페이지가 어떤 프레임에 있는지 기록한 매핑 테이블 |

---

## 4️⃣ 왜 가상 메모리를 사용할까?

### ① RAM 크기 한계 극복

실제 RAM: 8GB
실행 중인 프로그램들의 합산 메모리: 20GB

해결책:

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-4.png?raw=true"/>

### ② 프로세스 격리 및 보호

각 프로세스는 **자신만의 페이지 테이블**을 가진다.  
다른 프로세스의 메모리에 접근하려 해도 자신의 페이지 테이블에 없으므로 **OS가 즉시 차단**한다.

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-5.png?raw=true"/>

### ③ 메모리 효율적 공유

여러 프로세스가 **같은 라이브러리를 사용할 때** 물리 메모리에 하나만 올려두고 공유할 수 있다.

```
프로세스 A              물리 RAM
프로세스 B    ─────▶   java.lang.String 클래스 코드 (1개만 존재)
프로세스 C
→ RAM 공간 절약
```

---

## 5️⃣ 페이지 폴트(Page Fault)와 스래싱(Thrashing)

### 페이지 폴트 — CPU가 요청한 페이지가 RAM에 없는 상황

```
CPU: "Page 3의 데이터 필요해!"
        ↓
MMU: 페이지 테이블 확인 → RAM에 없음 (Page Fault 발생)
        ↓
OS 개입:
① 디스크 Swap 영역에서 Page 3 탐색
② RAM의 덜 쓰이는 페이지를 Swap으로 내보냄 (Page Out)
③ Page 3을 RAM으로 올림 (Page In)
④ 페이지 테이블 업데이트
⑤ CPU에게 재시도 지시
        ↓
소요 시간: RAM 접근(~100ns) vs 디스크 접근(~10ms) → 약 10만 배 느림 ⚠️
```

### 스래싱(Thrashing) — 페이지 폴트가 너무 잦아 성능이 붕괴되는 현상

```
RAM이 부족한 상황에서 프로세스가 너무 많을 때:

Page In → 다른 Page Out → 또 Page In → 또 Page Out ...
               ↓
    CPU는 정작 연산을 못 하고
    디스크 I/O만 계속 발생
               ↓
    시스템 전체 성능 급락 → Thrashing ❌
```

**스래싱 방지 방법:**

| 방법 | 설명 |
|------|------|
| 물리 RAM 증설 | 근본적 해결 |
| 실행 프로세스 수 제한 | OS가 다중 프로그래밍 정도(Degree) 조절 |
| Working Set 모델 | 프로세스가 자주 쓰는 페이지 집합을 RAM에 유지 |

---

## 6️⃣ Java 개발자 관점 — 가상 메모리와 JVM

### JVM 프로세스의 가상 주소 공간

<img src="https://github.com/devKobe24/images2/blob/main/virtual-memory-6.png?raw=true"/>

### `-Xmx`와 가상 메모리의 관계

```java
// JVM 시작 옵션
java -Xms512m -Xmx4g MyApp
//   ↑ 초기 Heap   ↑ 최대 Heap
```

```
-Xmx4g로 설정하면:
→ JVM이 OS에 4GB의 가상 주소 공간을 예약
→ 실제 물리 RAM을 4GB 즉시 점유하진 않음
→ Heap 사용량이 늘어날수록 실제 RAM 페이지 할당
→ 물리 RAM 부족 시 Swap 발생 → GC 성능 급락 ⚠️
```

> 💡 **실무 팁:** 가상 메모리 Swap이 발생하면 GC 시간이 급격히 늘어난다.  
> `-Xmx`는 물리 RAM의 70~80% 이내로 설정하는 것이 일반적인 권장 사항이다.

---

## 7️⃣ 핵심 정리

```
논리 주소 ──(MMU 변환)──▶ 물리 주소
  (프로세스가 사용)          (실제 RAM)

RAM에 없으면? → Page Fault → 디스크(Swap)에서 로드 → 느림
너무 자주 없으면? → Thrashing → 시스템 성능 붕괴
```

| 개념 | 한 줄 요약 |
|------|-----------|
| **가상 메모리** | 프로세스에게 독립적이고 거대한 메모리 공간 환상 제공 |
| **MMU** | 논리 주소 → 물리 주소 실시간 변환 하드웨어 |
| **페이징** | 메모리를 4KB 단위 페이지/프레임으로 나눠 관리 |
| **페이지 폴트** | 요청 페이지가 RAM에 없어 디스크에서 로드하는 이벤트 |
| **스래싱** | 페이지 폴트 과다로 CPU가 연산보다 I/O에 묶이는 현상 |
| **Java 관점** | `-Xmx` 초과 시 Swap 발생 → GC 성능 급락, 물리 RAM 70~80% 이내 권장 |
