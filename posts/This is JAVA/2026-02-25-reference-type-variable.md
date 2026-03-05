---
title: 🔗 참조 타입 변수의 == , != 연산
published: true
tags:
    - Java
    - Programming
    - Basic
    - Language
    - ComputerScience
    - Data Type
    - Reference Type

date: 2026-02-25
thumbnail: /assets/img/thumbnail/java.jpg
---

# 🔗 참조 타입 변수의 == , != 연산

> "참조 타입의 == 은 '값이 같냐'가 아니라 '같은 객체를 가리키냐'를 묻는다."

---

## 🔵 핵심 원리

참조 타입 변수에 저장된 것은 **객체 자체가 아니라 객체의 Heap 번지(주소)** 다.
따라서 `==`, `!=` 연산은 **번지가 같은지**를 비교한다.

| 연산 결과 | 의미 |
|-----------|------|
| `refA == refB` → `true` | 두 변수가 **같은 객체**를 가리킴 |
| `refA == refB` → `false` | 두 변수가 **다른 객체**를 가리킴 |

---

## 🧱 객체 참조 비교

```java
// refVar1 → 객체1 (단독 참조)
// refVar2 → 객체2
// refVar3 → 객체2 (refVar2와 같은 번지)
```

<img src="https://github.com/devKobe24/images2/blob/main/reference-type-variable-1.png?raw=true"/>

```java
refVar1 == refVar2;    // false ← 서로 다른 번지 (100 ≠ 200)
refVar1 != refVar2;    // true

refVar2 == refVar3;    // true  ← 같은 번지 (200 == 200)
refVar2 != refVar3;    // false
```

> 💡 `refVar2 == refVar3` 이 `true`인 이유는 값이 같아서가 아니라,
> **두 변수가 Heap의 동일한 주소(200번지)를 저장하고 있기 때문**이다.

---

## 📦 배열에서의 참조 비교

배열도 참조 타입이므로 동일한 원리가 적용된다.
**내용이 같아도 `new`로 따로 생성하면 다른 객체**다.

```java
int[] arr1 = new int[] { 1, 2, 3 };  // Heap 10번지에 새 배열 생성
int[] arr2 = new int[] { 1, 2, 3 };  // Heap 20번지에 새 배열 생성
int[] arr3 = arr2;                    // arr2의 번지(20번지)를 arr3에 복사
```

<img src="https://github.com/devKobe24/images2/blob/main/reference-type-variable-2.png?raw=true"/>

```java
System.out.println(arr1 == arr2);  // false ← 내용은 같지만 다른 객체 (10 ≠ 20)
System.out.println(arr2 == arr3);  // true  ← 같은 객체 (20 == 20)
```

```
실행 결과

false
true
```

---

## ⚠️ 흔한 실수 — 내용 비교 vs 번지 비교

```java
String s1 = new String("hello");
String s2 = new String("hello");

System.out.println(s1 == s2);       // false ← 다른 객체 (번지 비교)
System.out.println(s1.equals(s2));  // true  ← 같은 내용 (값 비교)
```

| 비교 방법 | 무엇을 비교하는가 | 사용 상황 |
|-----------|------------------|-----------|
| `==` | 번지(주소) | 같은 객체인지 확인할 때 |
| `.equals()` | 실제 값(내용) | 내용이 같은지 확인할 때 |

> ⚠️ **문자열, 컬렉션 등 내용을 비교할 때는 반드시 `equals()`를 사용해야 한다.**
> `==`를 사용하면 예상치 못한 `false`가 반환될 수 있다.

---

## 🔄 참조 공유의 부작용

같은 객체를 여러 변수가 참조할 때, **한 변수를 통해 수정하면 다른 변수에도 즉시 반영**된다.

```java
int[] arr2 = new int[] { 1, 2, 3 };
int[] arr3 = arr2;  // 같은 배열 공유

arr2[0] = 99;                        // arr2를 통해 수정
System.out.println(arr3[0]);         // 99 ← arr3에도 반영됨!
System.out.println(arr2 == arr3);    // true ← 여전히 같은 객체
```

독립적인 배열이 필요하다면 **명시적으로 복사**해야 한다.

```java
int[] original = { 1, 2, 3 };
int[] copy = original.clone();          // 독립 복사본 생성

copy[0] = 99;
System.out.println(original[0]);        // 1 ← 원본 영향 없음 ✅
System.out.println(original == copy);   // false ← 다른 객체
```

---

## 📝 핵심 요약

| 항목 | 내용 |
|------|------|
| 참조 타입 `==` | 값 비교 ❌, **번지(주소) 비교** ✅ |
| 같은 번지 → `true` | 두 변수가 동일한 Heap 객체를 가리킴 |
| 다른 번지 → `false` | 내용이 같아도 `new`로 따로 생성하면 `false` |
| 내용 비교 | `==` 대신 **`.equals()`** 사용 |
| 참조 공유 주의 | 한 변수 수정 시 같은 객체를 가리키는 모든 변수에 반영됨 |
| 독립 복사 | `.clone()`, `Arrays.copyOf()` 등 사용 |
