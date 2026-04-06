---
title: 메모리 안의 정보 - 변수(Variable)
published: true
tags:
  - cs
  - 컴퓨터 구조
  - Programming
date: 2026-04-07
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 메모리 안의 정보 — 변수(Variable)

## 왜 데이터를 메모리에 저장해야 할까?

프로그램이 의미 있는 일을 하려면 **데이터를 기억**해야 한다.

단순한 계산값뿐 아니라, 루프가 몇 번 돌았는지, 게임 캐릭터가 지금 어디 있는지, 현재 시각이 몇 시인지 같은 **프로그램 내부 상태(state)** 도 모두 데이터다. 이 상태를 추적하지 못하면 프로그램은 아무것도 "기억"하지 못하는 빈 껍데기가 된다.

---

## 변수(Variable)란?

> **변수는 메모리 내 데이터 위치(주소)를 가리키는 이름표다.**

파일 캐비닛을 떠올려 보자. 폴더가 수백 개 있어도, 라벨만 잘 붙어 있으면 원하는 폴더를 바로 찾을 수 있다. 변수가 바로 그 **라벨** 역할을 한다.

<img src="https://raw.githubusercontent.com/devKobe24/images2/refs/heads/main/A_drawing_of_computer_memory_as_a_row_of_boxes.png"/>

- `Level`, `Score` 는 메모리 한 칸(box)을 사용한다.
- `AveScore` 처럼 소수점이 있는 부동소수점(float) 값은 더 큰 정밀도가 필요하므로 **두 칸**을 차지한다.

변수를 선언하는 순간, 시스템이 자동으로 메모리 위치를 잡아 준다. 개발자는 메모리 주소를 직접 알 필요 없이, **변수 이름** 만으로 그 위치의 데이터를 읽고 쓸 수 있다.

---

## 좋은 변수 이름이 중요한 이유

라벨이 `할 일`, `중요한 일`, `다른 할 일`처럼 모호하다면 어떤 폴더가 어떤 내용인지 파악하기 어렵다. 변수도 마찬가지다.

| 나쁜 예 | 좋은 예            |
| ------- | ------------------ |
| `a`     | `loopCount`        |
| `x`     | `playerScore`      |
| `flag`  | `isLoggedIn`       |
| `data`  | `coffeeRoastLevel` |

모호한 이름은 코드를 읽는 모든 사람(미래의 나 포함)을 혼란스럽게 만든다. 이름이 충분히 설명적이면 주석 없이도 코드가 스스로 말한다.

---

## 데이터 타입(Type)

많은 프로그래밍 언어에서 변수는 **타입(type)** 과 연결된다. 타입은 두 가지를 결정한다.

1. **얼마나 많은 메모리를 사용할 것인가**
2. **저장된 값을 어떻게 해석할 것인가**

### 주요 기본 타입

| 타입               | 설명             | 메모리    | 예시            |
| ------------------ | ---------------- | --------- | --------------- |
| `Integer`          | 정수             | 작음      | `42`, `-7`, `0` |
| `Float` / `Double` | 소수점 있는 실수 | 큼        | `3.14`, `21.0`  |
| `Boolean`          | 참/거짓 두 값만  | 매우 작음 | `true`, `false` |
| `String`           | 문자(열)         | 가변      | `"hello"`       |

```java
// 의사코드 (pseudocode) 표기: <타입>:<변수이름>
Integer: coffee_count = 5
Float: percentage_words_spelled_correctly = 21.0
Boolean: had_enough_coffee = false
```

> Java처럼 **정적 타입(statically typed)** 언어는 컴파일 시점에 타입을 확정한다.  
> Python처럼 **동적 타입(dynamically typed)** 언어는 런타임에 타입이 결정된다.

---

## 변수를 사용하는 기본 연산

### 값 대입(Assignment)

```java
coffee_count = coffee_count + 1  // 현재 값을 읽어 1 더한 뒤 다시 저장
```

### 산술 연산 (숫자 타입)

```java
score = score * 2       // 점수 두 배
average = total / count // 평균 계산
remainder = n % 3       // 나머지
```

### 불린 연산 (Boolean 타입)

```java
canEnter   = isAdult AND hasTicket   // 둘 다 참이어야 입장 가능
isWeekend  = isSaturday OR isSunday  // 하나라도 참이면 주말
isNotEmpty = NOT isEmpty             // 반전
```

---

## 변수가 없다면?

변수가 없으면 프로그램은 상태를 **추적(track)**, **평가(evaluate)**, **변경(update)** 할 수 없다.

- FOR 루프가 몇 번 돌았는지 셀 수 없다.
- 게임 점수를 기록할 수 없다.
- 로그인 여부를 판단할 수 없다.

즉, 변수는 프로그램에 **시간과 맥락(context)의 개념** 을 부여하는 가장 기본적인 도구다.

---

## 핵심 정리

| 개념          | 한 줄 요약                           |
| ------------- | ------------------------------------ |
| **변수**      | 메모리 주소에 붙인 이름표            |
| **타입**      | 값의 종류와 메모리 크기를 결정       |
| **좋은 이름** | 코드 가독성과 유지보수의 첫걸음      |
| **상태 추적** | 변수가 없으면 프로그램은 기억이 없다 |

> 💡 **Java 개발자라면?**  
> Java는 대표적인 정적 타입 언어다. `int`, `double`, `boolean`, `String` 등 타입을 명시적으로 선언해야 하며, 덕분에 컴파일 타임에 타입 오류를 잡을 수 있다. 변수 이름은 **camelCase** 관례를 따르는 것이 표준이다 (`coffeeCount`, `isLoggedIn`).
