---
title: 🔄 Java 인터페이스로 프로그래밍하기
published: true
tags:
    - Data Structures
    - Algorithms
    - Java

date: 2026-02-06
thumbnail: /assets/img/thumbnail/data-structures-and-algorithms.jpg
---

# 🔄 Java 인터페이스로 프로그래밍하기

> LinkedList에서 ArrayList로 한 줄만 바꾸면 끝! - 인터페이스의 힘

## 🎯 학습 목표

**핵심 개념**: 인터페이스로 프로그래밍하면 구현체를 쉽게 교체할 수 있다

이번 실습에서는:

- ✅ List 인터페이스 타입으로 선언
- ✅ LinkedList → ArrayList로 구현체만 교체
- ✅ 단 한 줄의 코드 수정으로 완료

---

## 📝 실습 1: 초기 코드 - LinkedList 사용

### 코드 구조

```java
import java.util.LinkedList;
import java.util.List;

public class ListClientExample {
    @SuppressWarnings("rawtypes")
    private List list;  // ✅ 인터페이스 타입으로 선언

    @SuppressWarnings("rawtypes")
    public ListClientExample() {
        list = new LinkedList();  // LinkedList 구현체 사용
    }

    @SuppressWarnings("rawtypes")
    public List getList() {
        return list;  // List 타입으로 반환
    }

    public static void main(String[] args) {
        ListClientExample lce = new ListClientExample();
        @SuppressWarnings("rawtypes")
        List list = lce.getList();
        System.out.println(list);
    }
}
```

### 핵심 포인트

| 위치                | 타입        | 의미        |
| ------------------- | ----------- | ----------- |
| `private List list` | 인터페이스  | 유연성 확보 |
| `new LinkedList()`  | 구체 클래스 | 실제 구현체 |
| `return list`       | 인터페이스  | 느슨한 결합 |

---

## 🧪 테스트 코드 작성

```java
import static org.junit.Assert.assertThat;
import static org.hamcrest.CoreMatchers.*;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ListClientExampleTest {

    @Test
    public void testListClientExample() {
        ListClientExample lce = new ListClientExample();
        @SuppressWarnings("rawtypes")
        List list = lce.getList();

        // ArrayList인지 확인
        assertThat(list, instanceOf(ArrayList.class));
    }
}
```

### 첫 실행 결과

```
❌ Test Failed!
Expected: instanceof ArrayList
     but: was LinkedList
```

**이유**: 현재 구현체가 LinkedList이기 때문

> ⚠️ **참고**: 이 테스트는 실습용입니다.
> 실무에서는 **구현체보다 인터페이스의 동작**을 테스트해야 합니다!

---

## ✅ 실습 2: ArrayList로 교체하기

### 수정된 코드

```java
import java.util.ArrayList;  // ⭐ import 변경
import java.util.List;

public class ListClientExample {
    @SuppressWarnings("rawtypes")
    private List list;  // ✅ 선언은 그대로

    @SuppressWarnings("rawtypes")
    public ListClientExample() {
        list = new ArrayList();  // ⭐ 단 한 줄만 수정!
    }

    @SuppressWarnings("rawtypes")
    public List getList() {
        return list;  // ✅ 반환 타입도 그대로
    }

    public static void main(String[] args) {
        ListClientExample lce = new ListClientExample();
        @SuppressWarnings("rawtypes")
        List list = lce.getList();
        System.out.println(list);
    }
}
```

### 변경 사항 정리

```diff
- import java.util.LinkedList;
+ import java.util.ArrayList;

public ListClientExample() {
-   list = new LinkedList();
+   list = new ArrayList();
}
```

### 테스트 결과

```
✅ Test Passed!
Expected: instanceof ArrayList
     got: ArrayList
```

---

## 🎨 인터페이스 프로그래밍의 장점

### Before: 구체 클래스로 프로그래밍 ❌

```java
private ArrayList list;  // 구체 클래스

public ArrayList getList() {  // 구체 클래스 반환
    return list;
}
```

**문제점:**

- 🔒 ArrayList에 강하게 결합됨
- 🔄 LinkedList로 바꾸려면 여러 곳 수정 필요
- 📦 클라이언트 코드도 함께 수정 필요

### After: 인터페이스로 프로그래밍 ✅

```java
private List list;  // 인터페이스

public List getList() {  // 인터페이스 반환
    return list;
}
```

**장점:**

- 🔓 느슨한 결합 (Loose Coupling)
- 🔄 구현체를 자유롭게 교체 가능
- 📦 클라이언트 코드는 수정 불필요

---

## ⚠️ 과다 지정(Over-Specification) 주의

### 나쁜 예시 ❌

```java
import java.util.ArrayList;

public class ListClientExample {
    private ArrayList list;  // ❌ 구체 클래스로 선언

    public ArrayList getList() {  // ❌ 구체 클래스로 반환
        return list;
    }
}
```

**문제:**

```java
// 클라이언트 코드
ArrayList list = lce.getList();  // ❌ ArrayList에 의존

// LinkedList로 바꾸고 싶다면?
// 1. ListClientExample 수정
// 2. 모든 클라이언트 코드 수정
// 3. 컴파일 에러 발생 가능
```

### 좋은 예시 ✅

```java
import java.util.List;
import java.util.ArrayList;

public class ListClientExample {
    private List list;  // ✅ 인터페이스로 선언

    public List getList() {  // ✅ 인터페이스로 반환
        return list;
    }
}
```

**장점:**

```java
// 클라이언트 코드
List list = lce.getList();  // ✅ List에만 의존

// LinkedList로 바꿔도
// 클라이언트 코드 수정 불필요!
```

---

## 🤔 핵심 질문과 답변

### Q1: List로 선언만 하면 되지 않나요?

```java
private List list;

public ListClientExample() {
    list = new List();  // ❌ 컴파일 에러!
}
```

**답변**: ❌ **인터페이스는 인스턴스화할 수 없습니다!**

**이유:**

- 인터페이스는 **추상적인 명세**일 뿐
- 실제 구현체가 있어야 객체 생성 가능
- `new` 키워드는 구체 클래스에만 사용 가능

```java
// 올바른 방법
private List list;  // 선언은 인터페이스

public ListClientExample() {
    list = new ArrayList();  // 생성은 구체 클래스
}
```

---

### Q2: 왜 생성자에서만 구체 클래스를 사용하나요?

```java
private List list;  // 인터페이스
public List getList() { return list; }  // 인터페이스

public ListClientExample() {
    list = new ArrayList();  // 유일하게 구체 클래스
}
```

**답변**: 구현체 교체를 **한 곳에서만** 하기 위해!

**의존성 집중:**

```
생성자 (1곳) → ArrayList 의존
↓
나머지 모든 코드 → List 인터페이스 의존
```

---

## 💡 실무 적용 사례

### Spring Boot에서의 활용

```java
// Service 인터페이스
public interface UserService {
    User findById(Long id);
}

// 구현체 1: JPA 사용
@Service
public class UserServiceJpaImpl implements UserService {
    @Override
    public User findById(Long id) {
        // JPA로 조회
    }
}

// 구현체 2: MyBatis 사용
@Service
public class UserServiceMyBatisImpl implements UserService {
    @Override
    public User findById(Long id) {
        // MyBatis로 조회
    }
}

// Controller - 인터페이스에만 의존
@RestController
public class UserController {
    private final UserService userService;  // ✅ 인터페이스

    public UserController(UserService userService) {
        this.userService = userService;
    }
}
```

### 장점 정리

| 상황          | 인터페이스 프로그래밍  | 구체 클래스 프로그래밍 |
| ------------- | ---------------------- | ---------------------- |
| 구현체 교체   | 1곳만 수정 ✅          | 모든 곳 수정 ❌        |
| 테스트 용이성 | Mock 객체 쉬움 ✅      | Mock 생성 어려움 ❌    |
| 확장성        | 새 구현체 추가 쉬움 ✅ | 기존 코드 수정 필요 ❌ |
| 결합도        | 낮음 (Loose) ✅        | 높음 (Tight) ❌        |

---

## 📚 핵심 원칙 정리

### 1️⃣ 선언은 인터페이스로

```java
List list;        // ✅ Good
ArrayList list;   // ❌ Bad
```

### 2️⃣ 생성은 구체 클래스로

```java
new ArrayList();  // ✅ Good
new List();       // ❌ 컴파일 에러
```

### 3️⃣ 반환도 인터페이스로

```java
public List getList() { }        // ✅ Good
public ArrayList getList() { }   // ❌ Bad
```

### 4️⃣ 구현 변경은 한 곳에서만

```java
public ListClientExample() {
    list = new ArrayList();  // 여기만 수정!
}
```

---

## 🎯 실습 체크리스트

- [ ] LinkedList → ArrayList 교체 완료
- [ ] Import 문 수정 확인
- [ ] 테스트 실행 및 성공 확인
- [ ] 선언부는 List 인터페이스 유지 확인
- [ ] 생성자만 수정했는지 확인
- [ ] 과다 지정의 위험성 이해

---

## 💬 마무리

> **"구현이 아닌 인터페이스로 프로그래밍하라"** - GoF 디자인 패턴

인터페이스로 프로그래밍하는 습관은:

- 🔄 유연한 코드 작성의 시작
- 🧪 테스트하기 쉬운 구조
- 🚀 확장 가능한 아키텍처의 기초

Spring의 의존성 주입(DI)도 이 원칙을 기반으로 합니다! 💪
