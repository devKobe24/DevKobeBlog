---
title: 🔌 인터페이스의 디폴트 메소드 (Default Method)
published: true
tags:
    - Java
    - Interface
    - OOP
    - Constants
    - Immutable
    - Java-Core
    - Default Method

date: 2026-03-01
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔌 인터페이스의 디폴트 메소드 (Default Method)

> **"인터페이스에 공통 기능을 미리 구현해두고, 필요하면 재정의해서 쓴다"**

---

## 🤔 왜 디폴트 메소드가 필요할까?

추상 메소드만 있던 인터페이스에는 한 가지 불편함이 있었다.

> 인터페이스에 메소드를 하나 추가하면?  
> → 그 인터페이스를 구현한 **모든 클래스**가 해당 메소드를 재정의해야 한다.

구현 클래스가 10개라면 10곳을 전부 수정해야 한다. 😓  
이 문제를 해결하기 위해 **Java 8** 부터 디폴트 메소드가 도입되었다.

디폴트 메소드는 **인터페이스에 기본 구현을 제공**하므로,  
구현 클래스는 필요할 때만 선택적으로 재정의하면 된다.

---

## 📌 선언 방법

```java
[public] default 리턴타입 메소드명(매개변수, ...) {
    // 실행부 있음!
}
```

| 구분 | 추상 메소드 | 디폴트 메소드 |
|------|-------------|---------------|
| 실행부 `{}` | ❌ 없음 | ✅ 있음 |
| 키워드 | `abstract` (생략 가능) | `default` (필수) |
| 구현 클래스 의무 | 반드시 재정의 | 선택적 재정의 |
| 접근 제한자 | `public` 자동 적용 | `public` 자동 적용 |

> 💡 디폴트 메소드 내부에서 **상수 필드**나 **추상 메소드**를 자유롭게 호출할 수 있다.

---

## 💻 코드 예시

### 1. 인터페이스에 디폴트 메소드 선언

무음(mute) 처리 기능을 디폴트 메소드로 추가해보자.

```java
public interface RemoteControl {

    // 상수 필드
    int MAX_VOLUME = 10;
    int MIN_VOLUME = 0;

    // 추상 메소드
    void turnOn();
    void turnOff();
    void setVolume(int volume);

    // 디폴트 메소드 — 공통 구현 제공
    default void setMute(boolean mute) {
        if (mute) {
            System.out.println("무음 처리합니다.");
            setVolume(MIN_VOLUME);  // 추상 메소드 + 상수 필드 활용
        } else {
            System.out.println("무음 해제합니다.");
        }
    }
}
```

`Television` 클래스는 `setMute()`를 **재정의하지 않아도** 그대로 사용할 수 있다.

```java
RemoteControl rc = new Television();
rc.turnOn();
rc.setVolume(5);
rc.setMute(true);
rc.setMute(false);
```

```
TV를 켭니다.
현재 TV 볼륨: 5
무음 처리합니다.
현재 TV 볼륨: 0
무음 해제합니다.
```

---

### 2. 구현 클래스에서 디폴트 메소드 재정의

`Television`의 무음 해제는 단순히 메시지만 출력하지만,  
`Audio`는 **무음 전 볼륨을 기억했다가 복원**하는 더 정교한 동작이 필요하다.

```java
public class Audio implements RemoteControl {

    private int volume;
    private int memoryVolume;  // 무음 전 볼륨 기억용

    @Override
    public void turnOn() { System.out.println("Audio를 켭니다."); }

    @Override
    public void turnOff() { System.out.println("Audio를 끕니다."); }

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

    // 디폴트 메소드 재정의 — default 키워드 제거, public 필수!
    @Override
    public void setMute(boolean mute) {
        if (mute) {
            this.memoryVolume = this.volume;  // 현재 볼륨 저장
            System.out.println("무음 처리합니다.");
            setVolume(RemoteControl.MIN_VOLUME);
        } else {
            System.out.println("무음 해제합니다.");
            setVolume(this.memoryVolume);     // 원래 볼륨으로 복원
        }
    }
}
```

---

### 3. Television vs Audio 비교 실행

```java
public class Main {
    public static void main(String[] args) {
        RemoteControl rc;

        // Television — 디폴트 메소드 그대로 사용
        rc = new Television();
        rc.turnOn();
        rc.setVolume(5);
        rc.setMute(true);
        rc.setMute(false);  // 볼륨 복원 없음

        System.out.println();

        // Audio — 디폴트 메소드 재정의하여 볼륨 복원
        rc = new Audio();
        rc.turnOn();
        rc.setVolume(5);
        rc.setMute(true);
        rc.setMute(false);  // 볼륨 5로 복원!
    }
}
```

```
TV를 켭니다.
현재 TV 볼륨: 5
무음 처리합니다.
현재 TV 볼륨: 0
무음 해제합니다.          ← 볼륨 복원 없음

Audio를 켭니다.
현재 Audio 볼륨: 5
무음 처리합니다.
현재 Audio 볼륨: 0
무음 해제합니다.
현재 Audio 볼륨: 5        ← 원래 볼륨으로 복원됨!
```

---

## ⚠️ 재정의 시 주의사항

```java
// ❌ 잘못된 재정의 — default 키워드 유지
@Override
default void setMute(boolean mute) { ... }

// ❌ 잘못된 재정의 — public 생략
@Override
void setMute(boolean mute) { ... }

// ✅ 올바른 재정의
@Override
public void setMute(boolean mute) { ... }
```

| 항목 | 규칙 |
|------|------|
| `default` 키워드 | 재정의 시 **반드시 제거** |
| `public` 접근 제한자 | 재정의 시 **반드시 명시** |
| `@Override` | 선택이지만 **붙이는 것을 권장** |

---

## 🔄 디폴트 메소드 활용 패턴

디폴트 메소드는 단순히 "기본값 제공"을 넘어 **템플릿 메소드 패턴**처럼 활용할 수 있다.

```java
public interface Validator<T> {

    boolean validate(T value);  // 추상 메소드 — 각자 구현

    // 디폴트 메소드 — 공통 로직 제공
    default boolean validateWithLog(T value) {
        boolean result = validate(value);  // 추상 메소드 호출
        System.out.println("[검증 결과] " + value + " → " + (result ? "통과" : "실패"));
        return result;
    }
}
```

각 구현 클래스는 핵심 로직(`validate`)만 구현하고,  
공통 처리(로깅, 예외 처리 등)는 디폴트 메소드가 담당한다.

---

## ✅ 핵심 요약

- 디폴트 메소드는 인터페이스에 **실행부를 가진 메소드**를 선언하는 방법 (Java 8+)
- `default` 키워드를 리턴 타입 앞에 붙여 선언
- 구현 클래스는 디폴트 메소드를 **선택적으로 재정의** 가능
- 재정의 시 `default` 는 **제거**, `public` 은 **필수**
- 인터페이스 확장 시 기존 구현 클래스를 **수정하지 않아도 되는** 유연성 제공
