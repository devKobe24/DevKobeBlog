---
title: 🔌 인터페이스와 구현 클래스 선언
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

# 🔌 인터페이스와 구현 클래스 선언

> "인터페이스는 계약서, 구현 클래스는 그 계약을 이행하는 실행자다."

---

## 🔵 인터페이스 선언

`class` 키워드 대신 `interface` 키워드를 사용한다.
파일 형태(`.java` → `.class`)는 클래스와 동일하지만, **선언 방식과 구성 멤버가 다르다.**

```java
interface 인터페이스명 { ... }          // default 접근 제한 (같은 패키지 내)
public interface 인터페이스명 { ... }   // public 접근 제한 (어디서나 사용 가능)
```

### 📋 인터페이스가 가질 수 있는 멤버

```java
public interface 인터페이스명 {
    // 1. public 상수 필드       (static final 자동 적용)
    // 2. public 추상 메서드     (선언부만 있고 구현부 없음)
    // 3. public 디폴트 메서드   (default 키워드, 구현부 있음 — Java 8+)
    // 4. public 정적 메서드     (static 키워드              — Java 8+)
    // 5. private 메서드         (내부 공통 로직 추출용       — Java 9+)
    // 6. private 정적 메서드    (내부 공통 로직 추출용       — Java 9+)
}
```

> 💡 **추상 메서드** = 선언부(이름, 파라미터, 리턴 타입)만 있고 `{ }` 실행부가 없는 메서드.
> 인터페이스의 추상 메서드는 `public abstract`가 자동으로 붙는다.

### RemoteControl 인터페이스 작성

```java
public interface RemoteControl {
    // public abstract 자동 적용
    void turnOn();
}
```

---

## 🏗️ 구현 클래스 선언

인터페이스의 추상 메서드는 **구현 클래스가 반드시 재정의(@Override)** 해야 한다.
구현 클래스는 `implements` 키워드로 어떤 인터페이스를 구현하는지 선언한다.

```java
public class 클래스명 implements 인터페이스명 {
    // 인터페이스의 모든 추상 메서드를 반드시 구현해야 함
}
```

### Television 구현 클래스

<img src="https://github.com/devKobe24/images2/blob/main/interface-and-implement-class-1.png?raw=true"/>

```java
public class Television implements RemoteControl {

    @Override
    public void turnOn() {
        System.out.println("TV를 켭니다.");
    }
}
```

> ⚠️ `implements RemoteControl`이 없으면 `RemoteControl` 타입 변수에 대입할 수 없다.

---

## 🔗 변수 선언과 구현 객체 대입

인터페이스는 **참조 타입**이므로 변수 타입으로 사용할 수 있다.

```java
RemoteControl rc;          // 인터페이스 타입 변수 선언
rc = new Television();     // 구현 객체 대입 (번지 저장)
rc.turnOn();               // → 실제 실행: Television의 turnOn()
```

또는 선언과 동시에 대입도 가능하다.

```java
RemoteControl rc = new Television();
```

### 📌 전체 흐름 예제

```java
public class RemoteControlExample {
    public static void main(String[] args) {
        RemoteControl rc;

        rc = new Television();   // Television 대입
        rc.turnOn();             // "TV를 켭니다."
    }
}
```

```
실행 결과

TV를 켭니다.
```

---

## 🔄 구현 객체 교체 — 다형성의 핵심

`rc` 변수에는 `RemoteControl`을 구현한 **어떤 객체든** 대입할 수 있다.
교체해도 호출하는 코드(`rc.turnOn()`)는 바뀌지 않는다.

### Audio 구현 클래스 추가

```java
public class Audio implements RemoteControl {

    @Override
    public void turnOn() {
        System.out.println("Audio를 켭니다.");
    }
}
```

### 객체 교체 흐름

<img src="https://github.com/devKobe24/images2/blob/main/interface-and-implement-class-2.png?raw=true"/>

```java
public class RemoteControlExample {
    public static void main(String[] args) {
        RemoteControl rc;

        rc = new Television();   // Television 대입
        rc.turnOn();             // "TV를 켭니다."

        rc = new Audio();        // Audio로 교체
        rc.turnOn();             // "Audio를 켭니다."
    }
}
```

```
실행 결과

TV를 켭니다.
Audio를 켭니다.
```

> ✅ `rc.turnOn()`을 호출하는 코드는 단 한 줄도 바뀌지 않았다.
> 구현 객체만 교체함으로써 다른 동작을 수행한다 — 이것이 **다형성(Polymorphism)** 이다.

---

## 🧩 인터페이스의 멤버 종류 상세

| 멤버 | 키워드 | 구현부 여부 | 도입 버전 |
|------|--------|------------|----------|
| 상수 필드 | `static final` 자동 | ❌ | - |
| 추상 메서드 | `public abstract` 자동 | ❌ | - |
| 디폴트 메서드 | `default` | ✅ | Java 8+ |
| 정적 메서드 | `static` | ✅ | Java 8+ |
| private 메서드 | `private` | ✅ | Java 9+ |

```java
public interface RemoteControl {
    int MAX_VOLUME = 10;            // 상수 필드 (public static final 자동)
    void turnOn();                  // 추상 메서드 (public abstract 자동)

    default void printInfo() {      // 디폴트 메서드 — 공통 구현 제공
        System.out.println("RemoteControl 인터페이스");
    }

    static RemoteControl create() { // 정적 메서드 — 팩토리 메서드 등에 활용
        return new Television();
    }
}
```

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| 인터페이스 선언 | `interface` 키워드, `.java` → `.class` 파일로 컴파일 |
| 추상 메서드 | 선언부만 있고 구현부 없음, `public abstract` 자동 적용 |
| 구현 클래스 | `implements` 키워드, 추상 메서드 모두 `@Override` 필수 |
| 인터페이스 변수 | 참조 타입, `null` 대입 가능 |
| 구현 객체 대입 | `RemoteControl rc = new Television()` |
| 객체 교체 | 변수에 다른 구현 객체 대입 → 다형성 실현, 호출 코드 변경 없음 |
