---
title: 🔌 인터페이스 (Interface) — 역할과 목적
published: true
tags:
    - Java
    - OOP
    - Interface
    - Polymorphism
    - Loose-Coupling
    - Software-Design

date: 2026-02-26
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔌 인터페이스 (Interface) — 역할과 목적

> "인터페이스는 객체와 객체 사이를 연결하는 표준 계약서다."

---

## 🔵 인터페이스란?

**인터페이스(Interface)** = 두 객체를 연결하는 **중간 접속기(Connector)**

객체 A가 객체 B를 사용하고 싶을 때, 직접 연결하는 대신 **인터페이스를 통해 간접 연결**한다.

<img src="https://github.com/devKobe24/images2/blob/main/Interface-purpose-1.png?raw=true"/>

---

## ❓ 왜 직접 호출하지 않고 인터페이스를 거치는가?

객체 A가 객체 B를 **직접** 사용하면 단기적으로는 간단해 보인다.
하지만 나중에 객체 B를 객체 C로 교체해야 하는 순간, 문제가 생긴다.

### 직접 연결 — 교체 시 객체 A도 수정해야 함 ❌

```
[직접 연결]

객체 A  ──────────────────────►  객체 B
         객체 B의 메소드 직접 호출

→ 객체 B를 객체 C로 교체하면?
→ 객체 A의 코드도 전부 수정해야 함 💥
```

### 인터페이스 연결 — 교체해도 객체 A는 그대로 ✅

<img src="https://github.com/devKobe24/images2/blob/main/Interface-purpose-2.png?raw=true"/>

→ 객체 A는 인터페이스만 바라보므로, B→C 교체에 전혀 영향 없음 ✅
→ 객체 A의 코드 수정 없음 ✅

> 💡 객체 A 입장에서는 "인터페이스에 정해진 메소드를 호출할 뿐"이다.
> 뒤에서 실제로 누가 실행하는지(B인지 C인지)는 알 필요가 없다.

---

## 🧪 코드로 이해하기

### 인터페이스 정의

```java
// 인터페이스 = 두 객체 사이의 계약서
public interface Tire {
    void roll();  // 어떤 타이어든 반드시 roll()을 구현해야 한다
}
```

### 구현 객체 (객체 B, 객체 C)

```java
// 객체 B
public class HankookTire implements Tire {
    @Override
    public void roll() {
        System.out.println("한국 타이어가 굴러갑니다.");
    }
}

// 객체 C (교체 대상)
public class KumhoTire implements Tire {
    @Override
    public void roll() {
        System.out.println("금호 타이어가 굴러갑니다.");
    }
}
```

### 객체 A — 인터페이스만 바라본다

```java
public class Car {
    private Tire tire;  // 인터페이스 타입으로 선언

    public Car(Tire tire) {
        this.tire = tire;
    }

    public void run() {
        tire.roll();  // 어떤 타이어인지 몰라도 된다
    }
}
```

### 실행 — 교체 시 Car 코드는 그대로

```java
Car car1 = new Car(new HankookTire());  // 객체 B 사용
car1.run();  // "한국 타이어가 굴러갑니다."

Car car2 = new Car(new KumhoTire());    // 객체 C로 교체
car2.run();  // "금호 타이어가 굴러갑니다."

// Car 클래스 코드는 한 줄도 바꾸지 않았다 ✅
```

---

## 🎭 인터페이스와 다형성

같은 인터페이스 타입으로 **서로 다른 구현 객체**를 다룰 수 있다.
이것이 **다형성(Polymorphism)** 이다.

```java
Tire tire;

tire = new HankookTire();
tire.roll();  // "한국 타이어가 굴러갑니다."

tire = new KumhoTire();
tire.roll();  // "금호 타이어가 굴러갑니다."
```

```
Tire (인터페이스)
  ├── HankookTire → roll() : "한국 타이어"
  ├── KumhoTire   → roll() : "금호 타이어"
  └── MichelinTire → roll() : "미쉐린 타이어"  ← 나중에 추가해도 Car 수정 불필요
```

> 💡 **상속**으로도 다형성을 구현할 수 있지만,
> 자바는 **단일 상속만 허용**하기 때문에 **인터페이스를 이용한 다형성이 더 많이 쓰인다.**
> 클래스는 여러 인터페이스를 동시에 구현(`implements`)할 수 있다.

---

## ⚖️ 직접 연결 vs 인터페이스 연결 비교

| 항목 | 직접 연결 | 인터페이스 연결 |
|------|-----------|----------------|
| 구현 객체 교체 시 | 호출 측 코드 수정 필요 ❌ | 호출 측 코드 수정 불필요 ✅ |
| 결합도 | 강결합 (tight coupling) | 느슨한 결합 (loose coupling) |
| 유연성 | 낮음 | 높음 |
| 테스트 용이성 | 어려움 | 쉬움 (Mock 객체 교체 가능) |
| 다형성 | 제한적 | 자유롭게 구현 가능 |

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| 인터페이스 역할 | 두 객체를 느슨하게 연결하는 중간 계약서 |
| 핵심 장점 | 구현 객체를 교체해도 호출 측 코드 수정 불필요 |
| 다형성 | 같은 인터페이스로 다른 구현 객체를 자유롭게 교체 |
| 상속 vs 인터페이스 | 단일 상속 한계 → 인터페이스가 다형성 구현의 주된 방법 |
| 결합도 | 직접 참조(강결합) → 인터페이스(느슨한 결합)로 설계 권장 |
