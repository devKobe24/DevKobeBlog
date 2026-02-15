---
title: 🏗️ Java 클래스 — 객체를 만드는 설계도
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Class
    - Object
    - OOP
    - Constructor
    - Overloading
    - ComputerScience

date: 2026-02-15
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🏗️ Java 클래스 — 객체를 만드는 설계도

> **핵심 아이디어 :** 클래스는 객체를 찍어내는 설계도다. 설계도(클래스) 하나로 여러 개의 객체(인스턴스)를 만들 수 있다.

---

## 1️⃣ 클래스와 인스턴스

```
클래스  →  객체를 생성하기 위한 설계도
인스턴스  →  클래스로부터 만들어진 실제 객체
인스턴스화  →  클래스 → 객체를 만드는 과정
```

하나의 설계도로 여러 대의 자동차를 만들 수 있듯,  
하나의 클래스로 여러 개의 인스턴스를 생성할 수 있다.

---

## 2️⃣ 클래스 선언

```java
// 파일명과 클래스명은 반드시 동일해야 한다 (SportsCar.java)
public class SportsCar {
}
```

**클래스명 작성 규칙**

| 구분          | 내용                      |
| ------------- | ------------------------- |
| **스타일**    | 첫 글자 대문자, camelCase |
| **사용 가능** | 문자, 숫자, `$`, `_`      |
| **금지**      | 숫자로 시작               |

> 💡 하나의 소스 파일에 클래스를 여러 개 선언할 수 있지만, `public class`는 파일명과 동일한 클래스 하나뿐이다. 특별한 이유가 없다면 **파일 하나에 클래스 하나**를 권장한다.

---

## 3️⃣ 객체 생성 — new 연산자

```java
// 클래스 변수 = new 클래스();
Student s1 = new Student();  // Heap에 객체 생성 후 주소를 s1에 저장
Student s2 = new Student();  // 별개의 객체가 또 생성됨
```

**클래스의 두 가지 역할**

| 종류                  | 설명                 | 예시             |
| --------------------- | -------------------- | ---------------- |
| **라이브러리 클래스** | 다른 클래스에서 이용 | `Student`        |
| **실행 클래스**       | `main()` 메서드 보유 | `StudentExample` |

---

## 4️⃣ 클래스의 3가지 구성 멤버

```java
public class ClassName {
    int fieldName;           // 필드(field)    : 객체의 데이터 저장
    ClassName() { ... }     // 생성자(constructor) : 객체 생성 시 초기화
    void methodName() { ... } // 메서드(method) : 객체의 동작 정의
}
```

---

## 5️⃣ 필드(Field)

### 필드 선언과 기본값

초기값 없이 선언하면 타입에 따라 **자동으로 기본값**으로 초기화된다.

```java
public class Car {
    String model;    // null
    int speed;       // 0
    boolean start;   // false
    Tire tire;       // null (참조 타입)
}
```

| 타입                                  | 기본값  |
| ------------------------------------- | ------- |
| 정수 (`byte`, `short`, `int`, `long`) | `0`     |
| 실수 (`float`, `double`)              | `0.0`   |
| 논리 (`boolean`)                      | `false` |
| 참조 (클래스, 배열, 인터페이스)       | `null`  |

### 필드 vs 지역변수

| 구분          | 필드             | 지역변수                   |
| ------------- | ---------------- | -------------------------- |
| **선언 위치** | 클래스 블록      | 생성자·메서드 블록         |
| **존재 위치** | 객체 내부 (Heap) | 메서드 호출 시에만 (Stack) |
| **사용 범위** | 객체 내·외부     | 해당 블록 내부만           |

### 필드 사용 — 도트(`.`) 연산자

```java
Car myCar = new Car();

// 외부에서 필드 읽기·변경
System.out.println(myCar.speed);  // 읽기
myCar.speed = 60;                 // 변경
```

---

## 6️⃣ 생성자(Constructor)

### 기본 생성자

생성자를 별도로 선언하지 않으면 컴파일러가 **자동으로 추가**한다.

```java
// 작성한 코드
public class Car { }

// 컴파일 후 실제 동작
public class Car {
    public Car() { }  // 자동 추가
}
```

개발자가 생성자를 직접 선언하면 기본 생성자는 자동 추가되지 않는다.

### 생성자에서 필드 초기화 + `this` 키워드

```java
public class Korean {
    String nation = "대한민국";  // 공통값 → 필드 선언 시 초기화
    String name;                 // 개인마다 다름 → 생성자에서 초기화
    String ssn;

    public Korean(String name, String ssn) {
        this.name = name;  // this = 현재 객체 / this.name = 필드
        this.ssn = ssn;
    }
}
```

```java
Korean k1 = new Korean("박자바", "011225-1234567");
Korean k2 = new Korean("김자바", "930525-0654321");
// 같은 클래스지만 서로 다른 name, ssn을 가진 독립 객체
```

> 💡 매개변수명과 필드명이 같을 때는 반드시 `this.필드명`으로 구분한다.

---

## 7️⃣ 생성자 오버로딩(Overloading)

매개변수의 **타입·개수·순서**를 다르게 해서 생성자를 여러 개 선언할 수 있다.

```java
public class Car {
    Car() { ... }
    Car(String model) { ... }
    Car(String model, String color) { ... }
    Car(String model, String color, int maxSpeed) { ... }
}

// 호출 시 매개값에 따라 자동으로 생성자 선택
Car car1 = new Car();
Car car2 = new Car("그랜저");
Car car3 = new Car("그랜저", "흰색");
Car car4 = new Car("그랜저", "흰색", 300);
```

### `this()`로 생성자 간 중복 제거

오버로딩된 생성자 사이에 중복 코드가 많다면, `this()`로 한 생성자에 위임한다.

```java
// ❌ 중복 코드 발생
Car(String model) {
    this.model = model;
    this.color = "은색";
    this.maxSpeed = 250;
}
Car(String model, String color) {
    this.model = model;
    this.color = color;
    this.maxSpeed = 250;
}

// ✅ this()로 위임
Car(String model) {
    this(model, "은색", 250);   // 아래 생성자 호출
}
Car(String model, String color) {
    this(model, color, 250);    // 아래 생성자 호출
}
Car(String model, String color, int maxSpeed) {
    this.model = model;         // 실제 초기화는 여기서만
    this.color = color;
    this.maxSpeed = maxSpeed;
}
```

> ⚠️ `this()`는 반드시 **생성자의 첫 줄**에 위치해야 한다.

---

## 📌 핵심 요약

```
클래스  =  필드 + 생성자 + 메서드
new     =  Heap에 객체 생성 후 주소 반환
필드    =  초기값 없으면 타입별 기본값으로 자동 초기화
this    =  현재 객체 참조 (필드명·매개변수명 충돌 해결)
this()  =  같은 클래스 내 다른 생성자 호출 (중복 제거)
```
