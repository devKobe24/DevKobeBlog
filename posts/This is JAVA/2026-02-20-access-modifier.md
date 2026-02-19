---
title: 🔐 Java 접근 제한자(Access Modifier) — 보호와 캡슐화
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - AccessModifier
    - ComputerScience

date: 2026-02-20
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔐 Java 접근 제한자(Access Modifier) — 보호와 캡슐화

> **핵심 개념 :** 접근 제한자는 클래스, 필드, 생성자, 메서드에 대한 접근 권한을 제어하여 객체의 무결성을 유지한다. 4가지 수준이 있다: public, protected, default, private.

---

## 1️⃣ 접근 제한자 — 4가지 레벨

| 접근 제한자   | 적용 대상                    | 접근 범위                         |
| ------------- | ---------------------------- | --------------------------------- |
| **public**    | 클래스, 필드, 생성자, 메서드 | 제한 없음 (모든 곳에서 접근 가능) |
| **protected** | 필드, 생성자, 메서드         | 같은 패키지 + 자식 클래스         |
| **(default)** | 클래스, 필드, 생성자, 메서드 | 같은 패키지                       |
| **private**   | 필드, 생성자, 메서드         | 클래스 내부만                     |

> 💡 **default**는 키워드가 아니라 접근 제한자를 생략한 상태를 말한다.

### 접근 범위 비교

```
넓음  ←──────────────────────────────→  좁음
public > protected > default > private
```

---

## 2️⃣ 클래스 접근 제한 — public vs default

```java
public class A { }    // public: 모든 패키지에서 사용 가능
class B { }           // default: 같은 패키지에서만 사용 가능
```

### 접근 가능 여부

| 클래스 선언         | 같은 패키지 | 다른 패키지 |
| ------------------- | ----------- | ----------- |
| `public class A`    | ✅ 가능     | ✅ 가능     |
| `class B` (default) | ✅ 가능     | ❌ 불가능   |

### 예시

```java
// package1/A.java
package package1;
class A { }    // default 접근 제한

// package1/B.java
package package1;
public class B {
    A a;    // ✅ 같은 패키지 → 접근 가능
}

// package2/C.java
package package2;
public class C {
    A a;    // ❌ 다른 패키지 → 컴파일 에러
    B b;    // ✅ public 클래스 → 접근 가능
}
```

---

## 3️⃣ 생성자 접근 제한 — 객체 생성 제어

```java
public class ClassName {
    public ClassName() { }     // 모든 곳에서 객체 생성 가능
    ClassName() { }            // 같은 패키지에서만 객체 생성 가능
    private ClassName() { }    // 클래스 내부에서만 객체 생성 가능
}
```

### 접근 가능 여부

| 생성자      | 클래스 내부 | 같은 패키지 | 다른 패키지 |
| ----------- | ----------- | ----------- | ----------- |
| **public**  | ✅          | ✅          | ✅          |
| **default** | ✅          | ✅          | ❌          |
| **private** | ✅          | ❌          | ❌          |

### 예시

```java
public class A {
    public A(boolean b) { }    // public 생성자
    A(int i) { }               // default 생성자
    private A(String s) { }    // private 생성자
}

// 같은 패키지/B.java
class B {
    A a1 = new A(true);     // ✅ public 생성자
    A a2 = new A(1);        // ✅ default 생성자
    A a3 = new A("hi");     // ❌ private 생성자 (컴파일 에러)
}

// 다른 패키지/C.java
class C {
    A a1 = new A(true);     // ✅ public 생성자
    A a2 = new A(1);        // ❌ default 생성자 (컴파일 에러)
    A a3 = new A("hi");     // ❌ private 생성자 (컴파일 에러)
}
```

---

## 4️⃣ 필드와 메서드 접근 제한

<img src="https://github.com/devKobe24/images2/blob/main/filed-method-access-modifier-1.png?raw=true"/>

```java
public class A {
    public int field1;        // public 필드
    int field2;               // default 필드
    private int field3;       // private 필드

    public void method1() { } // public 메서드
    void method2() { }        // default 메서드
    private void method3() { } // private 메서드
}
```

### 접근 가능 여부

| 위치            | public | default | private |
| --------------- | ------ | ------- | ------- |
| **클래스 내부** | ✅     | ✅      | ✅      |
| **같은 패키지** | ✅     | ✅      | ❌      |
| **다른 패키지** | ✅     | ❌      | ❌      |

### 예시 — 같은 패키지

```java
// 같은 패키지/B.java
class B {
    void method() {
        A a = new A();

        a.field1 = 1;     // ✅ public
        a.field2 = 1;     // ✅ default
        a.field3 = 1;     // ❌ private (컴파일 에러)

        a.method1();      // ✅ public
        a.method2();      // ✅ default
        a.method3();      // ❌ private (컴파일 에러)
    }
}
```

### 예시 — 다른 패키지

```java
// 다른 패키지/C.java
class C {
    void method() {
        A a = new A();

        a.field1 = 1;     // ✅ public
        a.field2 = 1;     // ❌ default (컴파일 에러)
        a.field3 = 1;     // ❌ private (컴파일 에러)

        a.method1();      // ✅ public
        a.method2();      // ❌ default (컴파일 에러)
        a.method3();      // ❌ private (컴파일 에러)
    }
}
```

---

## 5️⃣ 클래스 내부에서는 모두 접근 가능

```java
public class A {
    private int field;
    private void method() { }

    public A() {
        this.field = 1;    // ✅ private 필드도 클래스 내부에서는 접근 가능
        this.method();     // ✅ private 메서드도 클래스 내부에서는 접근 가능
    }
}
```

> 💡 **중요:** 클래스 내부에서는 접근 제한자의 영향을 받지 않는다.

---

## 📌 핵심 요약

### 접근 제한자 비교

```
            클래스  같은    다른
            내부   패키지  패키지
public      ✅     ✅     ✅
protected   ✅     ✅     ✅(자식만)
default     ✅     ✅     ❌
private     ✅     ❌     ❌
```

### 적용 대상

| 요소       | public | protected | default | private |
| ---------- | ------ | --------- | ------- | ------- |
| **클래스** | ✅     | ❌        | ✅      | ❌      |
| **필드**   | ✅     | ✅        | ✅      | ✅      |
| **생성자** | ✅     | ✅        | ✅      | ✅      |
| **메서드** | ✅     | ✅        | ✅      | ✅      |

### 사용 원칙

```
✅ public      →  외부에 공개할 API
✅ protected   →  상속 관계에서만 공유
✅ default     →  같은 패키지 내에서만 공유
✅ private     →  클래스 내부에서만 사용 (정보 은닉)
```

### 캡슐화의 기본

```java
public class Person {
    private String name;      // 외부 접근 차단

    public String getName() { // public getter로 간접 접근 허용
        return name;
    }

    public void setName(String name) { // public setter로 간접 수정 허용
        this.name = name;
    }
}
```

> 🔒 **정보 은닉:** 필드는 private으로 감추고, getter/setter로 간접 접근하는 것이 안전하다.
