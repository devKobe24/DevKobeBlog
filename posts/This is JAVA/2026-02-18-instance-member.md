---
title: 🎯 Java 인스턴스 멤버 — 객체에 소속된 필드와 메서드
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Class
    - Instance
    - Static
    - Method
    - ComputerScience

date: 2026-02-18
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🎯 Java 인스턴스 멤버 — 객체에 소속된 필드와 메서드

> **핵심 개념 :** 인스턴스 멤버는 객체에 소속되어 있어 객체 생성 후에만 사용 가능하다. 정적 멤버는 클래스에 고정되어 객체 없이도 사용 가능하다.

---

## 1️⃣ 인스턴스 멤버 vs 정적 멤버

| 구분 | 소속 | 사용 조건 | 키워드 |
|------|------|-----------|--------|
| **인스턴스 멤버** | 객체 | 객체 생성 후 사용 가능 | (없음) |
| **정적 멤버** | 클래스 | 객체 없이도 사용 가능 | `static` |

---

## 2️⃣ 인스턴스 멤버 선언

```java
public class Car {
    // 인스턴스 필드
    int gas;
    
    // 인스턴스 메서드
    void setSpeed(int speed) { ... }
}
```

인스턴스 멤버는 **반드시 객체를 생성한 후** 사용할 수 있다.

```java
Car myCar = new Car();      // 객체 생성
myCar.gas = 10;             // 인스턴스 필드 접근
myCar.setSpeed(60);         // 인스턴스 메서드 호출
```

---

## 3️⃣ 메모리 구조 — 필드는 객체마다, 메서드는 공유

<img src = "https://github.com/devKobe24/images2/blob/main/instance-member-memory.png?raw=true"/>

```java
Car myCar = new Car();
myCar.gas = 10;
myCar.setSpeed(60);

Car yourCar = new Car();
yourCar.gas = 20;
yourCar.setSpeed(80);
```

### 메모리 배치

| 영역 | 저장 내용 | 특징 |
|------|-----------|------|
| **Stack** | `myCar`, `yourCar` 참조변수 | 각 변수는 Heap의 객체 주소 저장 |
| **Heap** | `myCar` 객체 (`gas=10`), `yourCar` 객체 (`gas=20`) | **필드는 객체마다 별도 존재** |
| **메서드 영역** | `setSpeed()` 메서드 코드 | **메서드는 공유** (중복 저장 방지) |

> 💡 **왜 메서드는 공유할까?**  
> 메서드는 코드 덩어리다. 객체마다 저장하면 메모리 낭비가 심하므로 한 곳에 두고 공유하되, 객체 없이는 사용 못하도록 제한을 건다.

---

## 4️⃣ this 키워드 — 나 자신을 가리키는 참조

`this`는 **현재 객체 자신**을 가리킨다. 주로 두 가지 상황에서 사용한다.

### 사용 이유 ① — 매개변수명과 필드명이 같을 때

```java
class Car {
    String model;
    int speed;
    
    Car(String model) {
        this.model = model;  // this 생략 불가 (구분 필요)
    }
    
    void setSpeed(int speed) {
        this.speed = speed;  // this 생략 불가
    }
}
```

```
매개변수 model  →  this.model (필드)로 대입
매개변수 speed  →  this.speed (필드)로 대입
```

### 사용 이유 ② — 명시적으로 현재 객체 강조

```java
void run() {
    this.setSpeed(100);  // this 생략 가능 (하지만 명확성 위해 사용)
    System.out.println(this.model + "가 달립니다.");
}
```

---

## 5️⃣ 실전 예시

```java
public class Car {
    String model;
    int speed;
    
    Car(String model) {
        this.model = model;
    }
    
    void setSpeed(int speed) {
        this.speed = speed;
    }
    
    void run() {
        this.setSpeed(100);
        System.out.println(this.model + "가 달립니다.(시속:" + this.speed + "km/h)");
    }
}
```

```java
Car myCar = new Car("포르쉐");
Car yourCar = new Car("벤츠");

myCar.run();   // 포르쉐가 달립니다.(시속:100km/h)
yourCar.run(); // 벤츠가 달립니다.(시속:100km/h)
```

---

## 📌 핵심 요약

| 개념 | 설명 |
|------|------|
| **인스턴스 멤버** | 객체에 소속, 객체 생성 후 사용 가능 |
| **필드** | 객체마다 별도 존재 (Heap) |
| **메서드** | 메서드 영역에서 공유 (중복 방지) |
| **this** | 현재 객체를 가리키는 참조 |
| **this 필수 사용** | 매개변수명 = 필드명일 때 |
