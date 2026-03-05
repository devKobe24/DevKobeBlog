---
title: 🔌 인터페이스의 추상 메소드 (Abstract Method)
published: true
tags:
    - Java
    - Interface
    - OOP
    - Constants
    - Immutable
    - Java-Core
    - Abstract Method

date: 2026-03-01
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔌 인터페이스의 추상 메소드 (Abstract Method)

> **"무엇을 해야 하는지는 정해두고, 어떻게 할지는 구현 클래스에게 맡긴다"**

---

## 🤔 추상 메소드란?

추상 메소드는 **선언부만 있고 실행부(`{}`)가 없는 메소드**다.

```java
[ public abstract ] 리턴타입 메소드명(매개변수, ...);
```

인터페이스에 선언된 메소드는 자동으로 `public abstract` 가 붙는다. 즉, 생략해도 컴파일러가 채워준다.

| 구분 | 인터페이스 (추상 메소드) | 구현 클래스 (재정의 메소드) |
|------|--------------------------|------------------------------|
| 역할 | **무엇을** 할지 선언 | **어떻게** 할지 구현 |
| 실행부 | ❌ 없음 | ✅ 있음 |
| 강제성 | 구현 클래스가 반드시 재정의 | @Override 로 구현 |

---

## 🔌 인터페이스가 중간에서 하는 일

<img src="https://github.com/devKobe24/images2/blob/main/abstract-method-1.png?raw=true"/>

- **객체 A** : 인터페이스를 통해 메소드를 호출하는 쪽
- **인터페이스** : 호출 방법(추상 메소드)만 정의 — 실행은 모름
- **객체 B** : 실제 실행 코드를 가진 구현 객체

덕분에 객체 A는 실제로 **Television인지 Audio인지 알 필요가 없다.**  
인터페이스라는 공통 창구만 보고 호출하면 된다 → **다형성의 핵심!**

---

## 💻 코드 예시

<img src="https://github.com/devKobe24/images2/blob/main/abstract-method-2.png?raw=true"/>

### 1. 인터페이스 선언

```java
public interface RemoteControl {

    // 상수 필드
    int MAX_VOLUME = 10;
    int MIN_VOLUME = 0;

    // 추상 메소드 (public abstract 생략됨)
    void turnOn();
    void turnOff();
    void setVolume(int volume);
}
```

### 2. 구현 클래스 — Television

```java
public class Television implements RemoteControl {

    private int volume;

    @Override
    public void turnOn() {
        System.out.println("TV를 켭니다.");
    }

    @Override
    public void turnOff() {
        System.out.println("TV를 끕니다.");
    }

    @Override
    public void setVolume(int volume) {
        // 상수 필드를 이용해 볼륨 범위 제한
        if (volume > RemoteControl.MAX_VOLUME) {
            this.volume = RemoteControl.MAX_VOLUME;
        } else if (volume < RemoteControl.MIN_VOLUME) {
            this.volume = RemoteControl.MIN_VOLUME;
        } else {
            this.volume = volume;
        }
        System.out.println("현재 TV 볼륨: " + this.volume);
    }
}
```

### 3. 구현 클래스 — Audio

```java
public class Audio implements RemoteControl {

    private int volume;

    @Override
    public void turnOn() {
        System.out.println("Audio를 켭니다.");
    }

    @Override
    public void turnOff() {
        System.out.println("Audio를 끕니다.");
    }

    @Override
    public void setVolume(int volume) {
        if (volume > RemoteControl.MAX_VOLUME) {
            this.volume = RemoteControl.MAX_VOLUME;
        } else if (volume < RemoteControl.MIN_VOLUME) {
            this.volume = RemoteControl.MIN_VOLUME;
        } else {
            this.volume = volume;
        }
        System.out.println("현재 Audio 볼륨: " + this.volume);
    }
}
```

### 4. 인터페이스 변수로 구현 객체 사용

```java
public class Main {
    public static void main(String[] args) {

        RemoteControl rc;  // 인터페이스 타입 변수 선언

        // Television 사용
        rc = new Television();
        rc.turnOn();
        rc.setVolume(5);
        rc.turnOff();

        // Audio 사용 — rc 변수는 그대로, 구현체만 교체!
        rc = new Audio();
        rc.turnOn();
        rc.setVolume(5);
        rc.turnOff();
    }
}
```

**실행 결과**
```
TV를 켭니다.
현재 TV 볼륨: 5
TV를 끕니다.
Audio를 켭니다.
현재 Audio 볼륨: 5
Audio를 끕니다.
```

> **핵심 포인트**: `rc` 변수 하나로 Television과 Audio를 **교체하며 사용**할 수 있다.  
> 호출하는 코드(`rc.turnOn()`)는 바꾸지 않았는데 실행 결과가 달라진다 → **다형성(Polymorphism)**

---

## ⚠️ 재정의 시 주의사항

### 접근 제한자는 낮출 수 없다

인터페이스의 추상 메소드는 기본이 `public` 이다.  
재정의할 때 `public` 보다 낮은 접근 제한자(`default`, `protected`, `private`)를 붙이면 **컴파일 에러** 가 발생한다.

```java
// ❌ 컴파일 에러
@Override
void turnOn() { ... }         // default 접근 → public보다 좁음, 불가!

// ✅ 정상
@Override
public void turnOn() { ... }  // public 유지
```

### 추상 메소드를 빠뜨리면 컴파일 에러

구현 클래스에서 인터페이스의 **모든** 추상 메소드를 재정의하지 않으면 컴파일 에러가 발생한다.  
일부만 구현하고 싶다면 해당 클래스를 `abstract class` 로 선언해야 한다.

```java
// setVolume()을 구현하지 않은 경우
public abstract class PartialRemote implements RemoteControl {
    @Override public void turnOn()  { ... }
    @Override public void turnOff() { ... }
    // setVolume()은 하위 클래스에 위임
}
```

---

## 🔄 인터페이스 변수의 동작 원리

```java
RemoteControl rc = new Television();
```

- `rc` 는 **참조 타입** 변수 → Television 객체의 메모리 주소(번지)를 저장
- `rc.turnOn()` 호출 시, 실제로는 Television의 `turnOn()` 이 실행됨
- `rc = new Audio()` 로 교체하면, 이후 호출은 Audio의 메소드가 실행됨

---

## ✅ 핵심 요약

| 항목 | 내용 |
|------|------|
| 추상 메소드 형식 | 실행부 `{}` 없이 선언부만 작성 |
| 자동 수식어 | `public abstract` 자동 적용 |
| 구현 클래스 의무 | 모든 추상 메소드 반드시 `@Override` 재정의 |
| 접근 제한자 | 재정의 시 반드시 `public` 유지 |
| 핵심 가치 | 구현체를 교체해도 호출 코드는 변경 없음 → **다형성** |
