---
title: ⚡ Java 정적(static) 멤버 — 객체 없이도 사용 가능
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

# ⚡ Java 정적(static) 멤버 — 객체 없이도 사용 가능

> **핵심 개념 :** 정적 멤버는 메서드 영역의 클래스에 고정되어 있어 객체 생성 없이 바로 사용 가능하다. 클래스 로더가 클래스를 메모리에 로딩하면 즉시 사용할 수 있다.

---

## 1️⃣ 정적 멤버의 메모리 구조

<img src="https://github.com/devKobe24/images2/blob/main/static-member.png?raw=true"/>

```
클래스 로더  →  바이트코드 읽기  →  메서드 영역에 클래스 저장
                                  ├── 정적 필드
                                  └── 정적 메서드
```

클래스가 메모리에 로딩되면 정적 멤버는 **객체 생성 없이 즉시 사용 가능**하다.

---

## 2️⃣ 정적 멤버 선언 — static 키워드

```java
public class Calculator {
    // 인스턴스 필드 (객체마다 다를 수 있음)
    String color;
    
    // 정적 필드 (모든 객체가 공유)
    static double pi = 3.14159;
    
    // 인스턴스 메서드 (인스턴스 필드 사용)
    void setColor(String color) {
        this.color = color;
    }
    
    // 정적 메서드 (인스턴스 필드 사용 안 함)
    static int plus(int x, int y) {
        return x + y;
    }
}
```

### 정적으로 선언해야 하는 경우

| 상황 | 이유 | 예시 |
|------|------|------|
| **모든 객체가 공유할 값** | 객체마다 가질 필요 없음 | `pi = 3.14159` |
| **인스턴스 필드를 안 쓰는 메서드** | 외부 매개값만으로 처리 | `plus(x, y)` |

---

## 3️⃣ 정적 멤버 사용 — 클래스명.멤버명

### ✅ 정석 : 클래스명으로 접근

```java
double result1 = 10 * 10 * Calculator.pi;
int result2 = Calculator.plus(10, 5);
int result3 = Calculator.minus(10, 5);
```

### ⚠️ 가능하지만 권장 안 함 : 객체 참조변수로 접근

```java
Calculator calc = new Calculator();
double result = calc.pi;  // 동작은 하지만 비권장
```

정적 멤버는 **클래스에 소속**되므로 클래스명으로 접근하는 것이 명확하다.

---

## 4️⃣ 정적 블록 — 복잡한 초기화

단순 초기화는 선언 시 바로 가능하다.

```java
static double pi = 3.14159;  // 간단한 초기화
```

복잡한 초기화는 **정적 블록** 사용한다.

```java
public class Television {
    static String company = "MyCompany";
    static String model = "LCD";
    static String info;
    
    static {
        // 클래스 로딩 시 자동 실행
        info = company + "-" + model;
    }
}

// 사용
System.out.println(Television.info);  // MyCompany-LCD
```

> 💡 정적 블록은 **클래스가 메모리에 로딩될 때 자동 실행**된다.  
> 여러 개 있으면 선언 순서대로 실행된다.

---

## 5️⃣ 핵심 제약 — 정적 메서드는 인스턴스 멤버 사용 불가

정적 메서드는 **객체 없이 실행**되므로 인스턴스 멤버를 사용할 수 없다.

```java
public class ClassName {
    int field1;              // 인스턴스 필드
    static int field2;       // 정적 필드
    
    void method1() { }       // 인스턴스 메서드
    static void method2() { } // 정적 메서드
    
    static {
        field1 = 10;   // ❌ 컴파일 에러
        method1();     // ❌ 컴파일 에러
        field2 = 10;   // ✅ 가능
        method2();     // ✅ 가능
    }
    
    static void method3() {
        this.field1 = 10;  // ❌ this 사용 불가
        field2 = 10;       // ✅ 가능
    }
}
```

### 정적 메서드에서 인스턴스 멤버 사용하려면?

**객체를 생성한 후** 접근하면 된다.

```java
static void method3() {
    ClassName obj = new ClassName();  // 객체 생성
    obj.field1 = 10;                  // 인스턴스 멤버 사용
    obj.method1();
}
```

---

## 6️⃣ main() 메서드도 정적 메서드다

`main()` 메서드는 **정적 메서드**이므로 같은 규칙이 적용된다.

```java
public class Car {
    int speed;              // 인스턴스 필드
    void run() { ... }      // 인스턴스 메서드
    
    public static void main(String[] args) {
        speed = 60;   // ❌ 컴파일 에러
        run();        // ❌ 컴파일 에러
    }
}
```

### ✅ 올바른 방법

```java
public static void main(String[] args) {
    Car myCar = new Car();  // 객체 생성
    myCar.speed = 60;       // 인스턴스 멤버 사용
    myCar.run();
}
```

---

## 📌 핵심 요약

| 구분 | 인스턴스 멤버 | 정적 멤버 |
|------|---------------|-----------|
| **소속** | 객체 | 클래스 |
| **키워드** | (없음) | `static` |
| **생성 시점** | 객체 생성 시 | 클래스 로딩 시 |
| **사용 조건** | 객체 생성 필수 | 객체 없이 가능 |
| **접근 방법** | `객체.멤버` | `클래스.멤버` ✅ |
| **메모리 위치** | Heap | 메서드 영역 |
| **this 사용** | 가능 | 불가능 |
| **인스턴스 멤버 접근** | 가능 | 불가능 (객체 생성 후만 가능) |

### 정적 멤버 사용 규칙

```
✅ 정적 멤버 → 정적 멤버    (가능)
✅ 정적 멤버 → 인스턴스 멤버  (객체 생성 후 가능)
✅ 인스턴스 멤버 → 정적 멤버  (가능)
✅ 인스턴스 멤버 → 인스턴스 멤버 (가능)
```
