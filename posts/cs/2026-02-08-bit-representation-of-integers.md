---
title: 🔢 정수의 비트 표현
published: true
tags:
    - cs
    - 컴퓨터 구조
    - Programming

date: 2026-02-08
thumbnail: /assets/img/thumbnail/cs.jpg
---

# 🔢 정수의 비트 표현

> 컴퓨터는 모든 숫자를 0과 1로만 저장합니다

## 🎯 핵심 개념

**컴퓨터는 2진수(Binary)만 이해합니다**

- 우리: 10진수 (0, 1, 2, ..., 9)
- 컴퓨터: 2진수 (0, 1)

---

## 📊 10진수 vs 2진수

### 10진수 (Decimal, Base-10)

**기본 원리:** 10의 거듭제곱

```
5,028 (10진수)
= 5×10³ + 0×10² + 2×10¹ + 8×10⁰
= 5×1000 + 0×100 + 2×10 + 8×1
= 5000 + 0 + 20 + 8
= 5,028
```

**자릿수:**

```
천의 자리  백의 자리  십의 자리  일의 자리
   10³       10²       10¹       10⁰
  1000       100        10         1
    5         0         2         8
```

---

### 2진수 (Binary, Base-2)

**기본 원리:** 2의 거듭제곱

```
1001110100100 (2진수) = 5,028 (10진수)

= 1×2¹² + 0×2¹¹ + 0×2¹⁰ + 1×2⁹ + 1×2⁸ + 1×2⁷ + 0×2⁶ + 1×2⁵ + 0×2⁴ + 0×2³ + 1×2² + 0×2¹ + 0×2⁰
= 1×4096 + 0×2048 + 0×1024 + 1×512 + 1×256 + 1×128 + 0×64 + 1×32 + 0×16 + 0×8 + 1×4 + 0×2 + 0×1
= 4096 + 512 + 256 + 128 + 32 + 4
= 5,028
```

**자릿수 (2의 거듭제곱):**

```
2¹²  2¹¹  2¹⁰  2⁹  2⁸  2⁷  2⁶  2⁵  2⁴  2³  2²  2¹  2⁰
4096 2048 1024 512 256 128  64  32  16   8   4   2   1
  1    0    0   1   1   1   0   1   0   0   1   0   0
```

---

## 💻 Java에서의 비트 표현

### Java의 정수 타입

| 타입    | 크기    | 비트 수 | 범위 (부호 있음)                                       |
| ------- | ------- | ------- | ------------------------------------------------------ |
| `byte`  | 1 byte  | 8 bits  | -128 ~ 127                                             |
| `short` | 2 bytes | 16 bits | -32,768 ~ 32,767                                       |
| `int`   | 4 bytes | 32 bits | -2,147,483,648 ~ 2,147,483,647                         |
| `long`  | 8 bytes | 64 bits | -9,223,372,036,854,775,808 ~ 9,223,372,036,854,775,807 |

### 코드 예제

```java
public class BinaryExample {
    public static void main(String[] args) {
        // 10진수 → 2진수 변환
        int decimal = 5028;
        String binary = Integer.toBinaryString(decimal);
        System.out.println("10진수: " + decimal);
        System.out.println("2진수: " + binary);
        // 출력: 1001110100100

        // 2진수 → 10진수 변환
        String binaryStr = "1001110100100";
        int decimalValue = Integer.parseInt(binaryStr, 2);
        System.out.println("2진수: " + binaryStr);
        System.out.println("10진수: " + decimalValue);
        // 출력: 5028

        // 리터럴로 2진수 직접 사용
        int num = 0b1001110100100;  // 0b 접두사
        System.out.println(num);  // 5028

        // 16진수 (0x 접두사)
        int hex = 0x13A4;  // 16진수
        System.out.println(hex);  // 5028
    }
}
```

---

## 📏 비트 수와 표현 범위

### 비트 수별 표현 가능한 양수 범위

| 비트 수 | 표현 가능한 값의 개수 | 범위 (0부터)      |
| ------- | --------------------- | ----------------- |
| 1 bit   | 2¹ = 2                | 0 ~ 1             |
| 2 bits  | 2² = 4                | 0 ~ 3             |
| 3 bits  | 2³ = 8                | 0 ~ 7             |
| 4 bits  | 2⁴ = 16               | 0 ~ 15            |
| 8 bits  | 2⁸ = 256              | 0 ~ 255           |
| 16 bits | 2¹⁶ = 65,536          | 0 ~ 65,535        |
| 32 bits | 2³² = 4,294,967,296   | 0 ~ 4,294,967,295 |

### 공식

```
n 비트로 표현 가능한 값의 개수 = 2ⁿ
범위: 0 ~ (2ⁿ - 1)
```

---

## 🔍 MSB와 LSB

### 용어 정의

```
  MSB                                    LSB
   ↓                                      ↓
[1][0][0][1][1][1][0][1][0][0][1][0][0]
 │                                       │
최상위 비트                          최하위 비트
(Most Significant Bit)        (Least Significant Bit)
```

**MSB (Most Significant Bit):**

- 가장 왼쪽 비트
- 변경 시 값의 변화가 가장 큼
- 부호 있는 정수에서 부호 비트로 사용

**LSB (Least Significant Bit):**

- 가장 오른쪽 비트
- 변경 시 값의 변화가 가장 작음
- 홀수/짝수 판별에 사용

### 실무 활용 예제

```java
public class BitPosition {
    public static void main(String[] args) {
        int num = 5028;  // 1001110100100

        // LSB 확인 (홀수/짝수 판별)
        boolean isOdd = (num & 1) == 1;
        System.out.println("홀수? " + isOdd);  // false (짝수)

        // MSB 확인 (부호 비트)
        int msb = num >>> 31;  // 32비트에서 최상위 비트
        System.out.println("MSB: " + msb);  // 0 (양수)

        // 특정 비트 위치 확인
        int position = 2;  // 2²의 자리
        boolean bitSet = ((num >> position) & 1) == 1;
        System.out.println(position + "번째 비트: " + bitSet);
    }
}
```

---

## 🔄 10진수 ↔ 2진수 변환

### 10진수 → 2진수 (수동 계산)

```
5,028을 2진수로 변환:

5028 ÷ 2 = 2514 ... 0  (LSB)
2514 ÷ 2 = 1257 ... 0
1257 ÷ 2 =  628 ... 1
 628 ÷ 2 =  314 ... 0
 314 ÷ 2 =  157 ... 0
 157 ÷ 2 =   78 ... 1
  78 ÷ 2 =   39 ... 0
  39 ÷ 2 =   19 ... 1
  19 ÷ 2 =    9 ... 1
   9 ÷ 2 =    4 ... 1
   4 ÷ 2 =    2 ... 0
   2 ÷ 2 =    1 ... 0
   1 ÷ 2 =    0 ... 1  (MSB)

역순으로 읽기: 1001110100100
```

### 2진수 → 10진수 (수동 계산)

```
1001110100100 (2진수)

= 1×4096 + 0×2048 + 0×1024 + 1×512 + 1×256 + 1×128 + 0×64 + 1×32 + 0×16 + 0×8 + 1×4 + 0×2 + 0×1
= 4096 + 512 + 256 + 128 + 32 + 4
= 5,028
```

### Java 코드로 변환

```java
public class BinaryConverter {

    // 10진수 → 2진수 (직접 구현)
    public static String decimalToBinary(int decimal) {
        if (decimal == 0) return "0";

        StringBuilder binary = new StringBuilder();
        int num = decimal;

        while (num > 0) {
            binary.insert(0, num % 2);  // 나머지를 앞에 추가
            num /= 2;
        }

        return binary.toString();
    }

    // 2진수 → 10진수 (직접 구현)
    public static int binaryToDecimal(String binary) {
        int decimal = 0;
        int length = binary.length();

        for (int i = 0; i < length; i++) {
            if (binary.charAt(length - 1 - i) == '1') {
                decimal += Math.pow(2, i);
            }
        }

        return decimal;
    }

    public static void main(String[] args) {
        // 테스트
        int num = 5028;
        String binary = decimalToBinary(num);
        System.out.println(num + " → " + binary);

        int result = binaryToDecimal(binary);
        System.out.println(binary + " → " + result);

        // Java 내장 메서드 사용
        System.out.println(Integer.toBinaryString(num));
        System.out.println(Integer.parseInt(binary, 2));
    }
}
```

---

## 🎨 리딩 제로 (Leading Zeros)

### 개념

```
13비트: 1001110100100
16비트: 0001001110100100  (앞에 0 3개 추가)
32비트: 00000000000000000001001110100100  (앞에 0 19개 추가)
```

**특징:**

- 값은 동일 (5,028)
- 컴퓨터는 고정된 비트 수 사용
- Java의 `int`는 항상 32비트

### 실무 예제

```java
public class LeadingZeros {
    public static void main(String[] args) {
        int num = 5028;

        // 32비트 전체 표현 (리딩 제로 포함)
        String binary32 = String.format("%32s",
            Integer.toBinaryString(num)).replace(' ', '0');
        System.out.println("32비트: " + binary32);

        // 16비트로 제한 (앞부분 잘림)
        String binary16 = String.format("%16s",
            Integer.toBinaryString(num)).replace(' ', '0');
        System.out.println("16비트: " + binary16);

        // 리딩 제로 개수 확인
        int leadingZeros = Integer.numberOfLeadingZeros(num);
        System.out.println("리딩 제로: " + leadingZeros);  // 19개
    }
}
```

---

## 💡 실무 활용 사례

### 1️⃣ 비트 플래그 (권한 관리)

```java
public class Permission {
    // 각 권한을 비트로 표현
    public static final int READ   = 1;  // 0001
    public static final int WRITE  = 2;  // 0010
    public static final int EXECUTE = 4;  // 0100
    public static final int DELETE = 8;  // 1000

    public static void main(String[] args) {
        // 권한 조합 (비트 OR)
        int userPermission = READ | WRITE;  // 0011 = 3

        // 권한 확인 (비트 AND)
        boolean canRead = (userPermission & READ) != 0;
        boolean canWrite = (userPermission & WRITE) != 0;
        boolean canExecute = (userPermission & EXECUTE) != 0;

        System.out.println("읽기: " + canRead);     // true
        System.out.println("쓰기: " + canWrite);    // true
        System.out.println("실행: " + canExecute);  // false

        // 권한 추가
        userPermission |= EXECUTE;  // 실행 권한 추가

        // 권한 제거
        userPermission &= ~WRITE;   // 쓰기 권한 제거
    }
}
```

### 2️⃣ 비트마스킹 (데이터 추출)

```java
public class BitMasking {
    public static void main(String[] args) {
        // RGB 색상을 int로 저장 (ARGB 형식)
        int color = 0xFF5733;  // 빨강: FF, 녹색: 57, 파랑: 33

        // 각 색상 성분 추출
        int red   = (color >> 16) & 0xFF;  // 상위 8비트
        int green = (color >> 8) & 0xFF;   // 중간 8비트
        int blue  = color & 0xFF;          // 하위 8비트

        System.out.println("Red: " + red);     // 255
        System.out.println("Green: " + green); // 87
        System.out.println("Blue: " + blue);   // 51

        // 색상 합성
        int newColor = (red << 16) | (green << 8) | blue;
        System.out.printf("Color: 0x%X%n", newColor);
    }
}
```

### 3️⃣ 효율적인 연산

```java
public class BitwiseOptimization {
    public static void main(String[] args) {
        int n = 100;

        // 2의 거듭제곱으로 곱하기/나누기
        int multiply2 = n << 1;   // n * 2 = 200
        int multiply4 = n << 2;   // n * 4 = 400
        int divide2 = n >> 1;     // n / 2 = 50
        int divide4 = n >> 2;     // n / 4 = 25

        // 홀수/짝수 판별
        boolean isEven = (n & 1) == 0;  // 빠름
        // vs
        boolean isEven2 = n % 2 == 0;   // 느림

        // 2의 거듭제곱 확인
        boolean isPowerOfTwo = (n & (n - 1)) == 0 && n != 0;

        System.out.println("2배: " + multiply2);
        System.out.println("1/2: " + divide2);
        System.out.println("짝수? " + isEven);
    }
}
```

### 4️⃣ IP 주소 저장

```java
public class IpAddress {
    public static void main(String[] args) {
        // IP 주소: 192.168.1.100
        int ip = (192 << 24) | (168 << 16) | (1 << 8) | 100;

        // IP 주소 추출
        int octet1 = (ip >> 24) & 0xFF;  // 192
        int octet2 = (ip >> 16) & 0xFF;  // 168
        int octet3 = (ip >> 8) & 0xFF;   // 1
        int octet4 = ip & 0xFF;          // 100

        System.out.printf("IP: %d.%d.%d.%d%n",
            octet1, octet2, octet3, octet4);
    }
}
```

---

## ⚠️ 오버플로우와 언더플로우

### 오버플로우 (Overflow)

**최대값을 초과할 때 발생**

```java
public class Overflow {
    public static void main(String[] args) {
        byte max = 127;  // byte 최대값 (0111_1111)
        byte overflow = (byte) (max + 1);

        System.out.println("Max: " + max);           // 127
        System.out.println("Overflow: " + overflow); // -128 (1000_0000)

        // int 오버플로우
        int intMax = Integer.MAX_VALUE;  // 2,147,483,647
        int intOverflow = intMax + 1;
        System.out.println("Int overflow: " + intOverflow);  // -2,147,483,648
    }
}
```

### 언더플로우 (Underflow)

**최소값을 벗어날 때 발생**

```java
public class Underflow {
    public static void main(String[] args) {
        byte min = -128;  // byte 최소값
        byte underflow = (byte) (min - 1);

        System.out.println("Min: " + min);              // -128
        System.out.println("Underflow: " + underflow);  // 127
    }
}
```

---

## 📊 다양한 진법 표현

### Java에서 지원하는 진법

```java
public class NumberSystems {
    public static void main(String[] args) {
        int decimal = 5028;

        // 2진수 (Binary)
        String binary = Integer.toBinaryString(decimal);
        System.out.println("2진수: " + binary);
        // 1001110100100

        // 8진수 (Octal)
        String octal = Integer.toOctalString(decimal);
        System.out.println("8진수: " + octal);
        // 11644

        // 16진수 (Hexadecimal)
        String hex = Integer.toHexString(decimal);
        System.out.println("16진수: " + hex);
        // 13a4

        // 리터럴로 직접 표현
        int bin = 0b1001110100100;    // 2진수
        int oct = 011644;              // 8진수 (0으로 시작)
        int hexNum = 0x13a4;           // 16진수 (0x로 시작)

        System.out.println(bin);       // 5028
        System.out.println(oct);       // 5028
        System.out.println(hexNum);    // 5028
    }
}
```

---

## 🎯 핵심 요약

### 비트와 진법

| 진법   | 기수 | 사용 숫자 | 예시          |
| ------ | ---- | --------- | ------------- |
| 2진수  | 2    | 0, 1      | 1001110100100 |
| 8진수  | 8    | 0-7       | 11644         |
| 10진수 | 10   | 0-9       | 5028          |
| 16진수 | 16   | 0-9, A-F  | 13A4          |

### Java 정수 타입

```
byte  (8비트)  → -128 ~ 127
short (16비트) → -32,768 ~ 32,767
int   (32비트) → -2,147,483,648 ~ 2,147,483,647
long  (64비트) → -9,223,372,036,854,775,808 ~ 9,223,372,036,854,775,807
```

### 핵심 공식

```
n비트 표현 범위: 0 ~ (2ⁿ - 1)
부호 있는 n비트: -2ⁿ⁻¹ ~ 2ⁿ⁻¹ - 1
```

---

## 💡 실무 팁

### 1️⃣ 비트 연산은 빠르다

```java
// ✅ 빠른 방법 (비트 연산)
int double = n << 1;      // *2
int half = n >> 1;        // /2
boolean even = (n & 1) == 0;

// ❌ 느린 방법 (산술 연산)
int double = n * 2;
int half = n / 2;
boolean even = n % 2 == 0;
```

### 2️⃣ 가독성 고려

```java
// ❌ 가독성 낮음
if ((flags & 0x04) != 0) { }

// ✅ 가독성 높음
private static final int EXECUTE = 0x04;
if ((flags & EXECUTE) != 0) { }
```

### 3️⃣ 타입 주의

```java
// ⚠️ byte는 쉽게 오버플로우
byte b = 100;
b = (byte) (b + 50);  // -106 (오버플로우!)

// ✅ int 사용 권장
int i = 100;
i = i + 50;  // 150
```

---

## 🚀 마무리

> **"컴퓨터는 2진수로 생각합니다. 개발자는 비트를 이해해야 합니다."**

Backend 개발자가 비트를 알아야 하는 이유:

- 🎯 **효율적인 알고리즘**: 비트 연산으로 성능 향상
- 🔐 **권한 관리**: 비트 플래그로 간결한 구현
- 🎨 **데이터 압축**: 비트 마스킹으로 공간 절약
- 🐛 **디버깅**: 오버플로우 문제 이해

비트 연산을 마스터하면 더 효율적이고 강력한 코드를 작성할 수 있습니다! 💪
