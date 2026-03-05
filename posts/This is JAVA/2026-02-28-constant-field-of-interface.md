---
title: 🔌 인터페이스의 상수 필드 (Constant Field)
published: true
tags:
    - Java
    - Interface
    - OOP
    - Constants
    - Immutable
    - Java-Core

date: 2026-02-28
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔌 인터페이스의 상수 필드 (Constant Field)

> 인터페이스는 단순한 규약 그 이상 — **공유 상수의 저장소**가 될 수 있다.

---

## 🤔 왜 인터페이스에 상수를 선언할까?

여러 클래스가 공통으로 사용해야 하는 **"변하지 않는 값"** 이 있다고 생각해보자.

예를 들어, 리모콘을 구현하는 TV, 에어컨, 선풍기 클래스가 모두 **최대 볼륨(10)** 과 **최소 볼륨(0)** 을 알아야 한다면?  
각 클래스마다 따로 선언하면 중복이 생기고, 값이 바뀔 때 여러 곳을 수정해야 하는 문제가 생긴다.

인터페이스에 상수를 선언하면 **단 한 곳에서 관리**하고, 구현 클래스들이 공유해서 사용할 수 있다.

---

## 📌 선언 방법

```java
[ public static final ] 타입 상수명 = 값;
```

인터페이스의 필드는 **자동으로** `public static final` 이 적용된다.  
즉, 생략해도 컴파일러가 자동으로 붙여준다.

| 수식어 | 의미 |
|--------|------|
| `public` | 어디서든 접근 가능 |
| `static` | 인스턴스 없이 인터페이스명으로 직접 접근 |
| `final` | 한 번 할당되면 변경 불가 (불변) |

> 💡 **네이밍 컨벤션**: 상수명은 **대문자**로 작성하고, 단어 사이는 **언더바(`_`)** 로 구분한다.  
> ex) `MAX_VOLUME`, `MIN_VALUE`, `DEFAULT_TIMEOUT`

---

## 💻 코드 예시

### 1. 인터페이스에 상수 선언

```java
public interface RemoteControl {

    int MAX_VOLUME = 10;  // 컴파일 시 → public static final int MAX_VOLUME = 10;
    int MIN_VOLUME = 0;   // 컴파일 시 → public static final int MIN_VOLUME = 0;

    void turnOn();
    void turnOff();
    void setVolume(int volume);
}
```

### 2. 구현 클래스에서 활용

```java
public class Television implements RemoteControl {

    private int volume;

    @Override
    public void setVolume(int volume) {
        // 상수를 활용해 볼륨 범위를 안전하게 제한
        if (volume > RemoteControl.MAX_VOLUME) {
            this.volume = RemoteControl.MAX_VOLUME;
        } else if (volume < RemoteControl.MIN_VOLUME) {
            this.volume = RemoteControl.MIN_VOLUME;
        } else {
            this.volume = volume;
        }
        System.out.println("TV 볼륨: " + this.volume);
    }

    @Override public void turnOn()  { System.out.println("TV 켜기"); }
    @Override public void turnOff() { System.out.println("TV 끄기"); }
}
```

### 3. 인터페이스로 직접 접근

```java
public class Main {
    public static void main(String[] args) {
        System.out.println("최대 볼륨: " + RemoteControl.MAX_VOLUME); // 10
        System.out.println("최소 볼륨: " + RemoteControl.MIN_VOLUME); // 0

        Television tv = new Television();
        tv.setVolume(15); // → 최대값 10으로 제한됨
        tv.setVolume(5);  // → 그대로 5
    }
}
```

**실행 결과**
```
최대 볼륨: 10
최소 볼륨: 0
TV 볼륨: 10
TV 볼륨: 5
```

---

## ⚠️ 주의사항

```java
public interface BadExample {
    int VALUE = 10;
}

// 상수 값 변경 시도 → 컴파일 에러!
// BadExample.VALUE = 20;  ← final이므로 불가능
```

상수는 선언과 동시에 반드시 **초기화**해야 하며, 이후 변경이 불가능하다.

---

## ✅ 핵심 요약

- 인터페이스 필드는 자동으로 `public static final` → **불변 상수**
- 상수명은 **UPPER_SNAKE_CASE** 관례를 따른다
- `인터페이스명.상수명` 으로 직접 접근 가능 (인스턴스 불필요)
- 여러 구현 클래스가 **공통 상수를 공유**할 때 유용하게 활용된다
