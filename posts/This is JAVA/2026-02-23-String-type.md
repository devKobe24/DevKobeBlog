---
title: 📘 Java 문자열 타입 (String)
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - Type
    - ComputerScience
    - reference
    - String

date: 2026-02-23
thumbnail: /assets/img/thumbnail/java.jpg
---

# 📘 Java 문자열 타입 (String)

> "자바에서 문자 하나와 문자열은 완전히 다른 타입이다."

---

## 🔤 char vs String — 뭐가 다를까?

| 구분 | 타입 | 예시 |
|------|------|------|
| 문자 1개 | `char` (기본 타입) | `'A'`, `'가'` |
| 문자열 | `String` (참조 타입) | `"Hello"`, `"홍길동"` |

> ⚠️ **핵심 규칙**: `char`는 작은따옴표(`' '`), `String`은 큰따옴표(`" "`)

```java
// ❌ 잘못된 코드 — char에 큰따옴표 사용 시 컴파일 에러
char var1 = "A";       // 컴파일 에러
char var2 = "홍길동";  // 컴파일 에러

// ✅ 올바른 코드
char c      = 'A';
String var1 = "A";
String var2 = "홍길동";
```

> 💡 `String`은 자바의 **기본 타입(primitive)이 아닌 참조 타입(reference type)** 이다.
> 내부적으로 문자들의 배열을 참조하는 객체다.

---

## 🪄 이스케이프 문자 (Escape Character)

문자열 안에서 역슬래시(`\`)를 앞에 붙여 **특수 문자나 특별한 출력 효과**를 만들 수 있다.

| 이스케이프 문자 | 의미 |
|----------------|------|
| `\"` | `"` 문자 포함 |
| `\'` | `'` 문자 포함 |
| `\\` | `\` 문자 포함 |
| `\t` | 탭(Tab) 간격 |
| `\n` | 줄바꿈 (Line Feed) |
| `\r` | 캐리지 리턴 (Carriage Return) |
| `\uXXXX` | 16진수 유니코드 문자 |

### 📌 예제

```java
public class StringExample {
    public static void main(String[] args) {
        String name = "강민성";
        String job  = "백엔드 개발자";
        System.out.println(name);
        System.out.println(job);

        // \" 로 큰따옴표 포함
        String str = "나는 \"자바\"를 배웁니다.";
        System.out.println(str);

        // \t 로 탭 정렬
        str = "번호\t이름\t직업";
        System.out.println(str);

        // \n 으로 줄바꿈
        System.out.println("나는\n자바를\n배웁니다.");
    }
}
```

```
실행 결과

강민성
백엔드 개발자
나는 "자바"를 배웁니다.
번호    이름    직업
나는
자바를
배웁니다.
```

---

## 🧱 텍스트 블록 (Text Block) — Java 13+

이스케이프 문자를 남발하지 않아도 되는 **멀티라인 문자열 문법**.
큰따옴표 3개(`"""`)로 감싸면 작성한 그대로 문자열로 저장된다.

### ✅ 기존 방식 vs 텍스트 블록 비교

```java
// ❌ 기존 방식 — 이스케이프로 지저분함
String str1 = "" +
        "{\n" +
        "\t\"id\":\"winter\",\n" +
        "\t\"name\":\"눈송이\"\n" +
        "}";

// ✅ 텍스트 블록 — 깔끔하고 직관적 (Java 13+)
String str2 = """
        {
            "id":"winter",
            "name":"눈송이"
        }
        """;
```

두 변수의 출력 결과는 **완전히 동일**하다.

```
{
    "id":"winter",
    "name":"눈송이"
}
```

### 📌 줄바꿈 없이 이어 쓰기 — Java 14+

텍스트 블록에서 줄 끝에 `\`를 붙이면 해당 줄에서 줄바꿈 없이 다음 줄로 이어진다.

```java
String str = """
        나는 자바를 \
        학습합니다.
        나는 자바 고수가 될 겁니다.
        """;
System.out.println(str);
```

```
실행 결과

나는 자바를 학습합니다.
나는 자바 고수가 될 겁니다.
```

---

## 🔢 문자열 주요 메서드

`String` 은 참조 타입인 만큼 다양한 내장 메서드를 제공한다.

| 메서드 | 설명 | 예시 |
|--------|------|------|
| `length()` | 문자열 길이 반환 | `"hello".length()` → `5` |
| `charAt(int)` | 특정 인덱스 문자 반환 | `"hello".charAt(1)` → `'e'` |
| `equals(String)` | 문자열 값 비교 | `"a".equals("a")` → `true` |
| `contains(String)` | 포함 여부 확인 | `"hello".contains("ell")` → `true` |
| `replace(old, new)` | 문자열 치환 | `"hello".replace("l","r")` → `"herro"` |
| `trim()` | 앞뒤 공백 제거 | `"  hi  ".trim()` → `"hi"` |
| `toUpperCase()` | 대문자 변환 | `"hello".toUpperCase()` → `"HELLO"` |
| `split(regex)` | 문자열 분리 | `"a,b,c".split(",")` → `["a","b","c"]` |

> ⚠️ **문자열 비교 시 `==` 대신 `equals()` 를 사용해야 한다.**
> `==` 는 참조(주소) 비교, `equals()` 는 값 비교다.

```java
String a = new String("hello");
String b = new String("hello");

System.out.println(a == b);       // false (서로 다른 객체)
System.out.println(a.equals(b));  // true  (값이 같음)
```

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| `char` | 문자 1개, 기본 타입, 작은따옴표 |
| `String` | 문자열, 참조 타입, 큰따옴표 |
| 이스케이프 문자 | `\"`, `\n`, `\t`, `\\` 등 특수 표현 |
| 텍스트 블록 | `"""..."""`, Java 13+, 멀티라인 문자열 |
| 줄바꿈 없이 이어쓰기 | 줄 끝에 `\` 사용, Java 14+ |
| 문자열 비교 | `==` 대신 반드시 `equals()` 사용 |
