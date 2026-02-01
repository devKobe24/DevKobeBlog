---
title: 🌵Spring Data JPA, 쿼리 메서드와 추천 상품 로직
tags:
  - Troubleshooting
  - Backend Development
  - Spring Data JPA
date: 2026-02-01
thumbnail: /assets/img/thumbnail/troubleshooting.jpg
---

# 🌵 Spring Data JPA로 다육식물 쇼핑몰 만들기

## 📚 목차

1. [findByCategoryIn - 마법같은 쿼리 메서드](#findbycategoryin)
2. [getHomeFeaturedProducts - 홈 화면 추천 상품 로직](#getHomeFeaturedProducts)
3. [핵심 요약](#Key-Takeaways)

---

<a id="findbycategoryin"></a>
## 1. findByCategoryIn - 마법같은 쿼리 메서드 🪄

### 📝 코드부터 보기

```java
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryIn(Collection<String> categories);
}
```

### 🤔 이게 어떻게 동작하는 거죠?

`findByCategoryIn(Collection<String> categories)`는 Spring Data JPA의 **쿼리 메서드(Query Method)** 기능입니다.
개발자가 SQL을 직접 작성하지 않아도, 메서드 이름만으로 복잡한 쿼리를 실행할 수 있는 강력한 기능이죠!

### ⚙️ 동작 원리

#### 1️⃣ SQL IN 연산자 자동 생성

JPA는 메서드 이름의 `In` 키워드를 분석하여 다음과 같은 SQL을 자동으로 생성합니다:

```sql
SELECT * FROM product WHERE category IN (?, ?, ?)
```

#### 2️⃣ 다중 조건 검색의 편리함

인자로 전달된 `Collection<String>`(List, Set 등)에 포함된 여러 카테고리 중 **하나라도 일치**하는 상품들을 모두 찾아옵니다.

**💡 실제 사용 예시:**

```java
List<String> leafCategories = List.of("에케베리아", "세덤", "하월시아");
List<Product> products = productRepository.findByCategoryIn(leafCategories);
// → "에케베리아", "세덤", "하월시아" 카테고리의 모든 상품을 조회!
```

#### 3️⃣ 추상화된 데이터 접근

개발자가 직접 SQL을 작성하지 않아도, 인터페이스 정의만으로 복잡한 다중 조건 조회를 안전하게 처리할 수 있습니다.

### ✅ 장점 정리

| 장점                   | 설명                                 |
| ---------------------- | ------------------------------------ |
| 🎯 **타입 안정성**     | 컴파일 타임에 오류 발견 가능         |
| 🧹 **깔끔한 코드**     | SQL 작성 없이 메서드 이름만으로 구현 |
| 🔒 **SQL 인젝션 방지** | PreparedStatement 자동 사용          |
| 📖 **가독성**          | 메서드 이름이 곧 문서                |

---

<a id="getHomeFeaturedProducts"></a>
## 2. getHomeFeaturedProducts - 홈 화면 추천 상품 로직 🏠

### 📝 전체 코드

```java
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ProductService {

    private static final int HOME_FEATURED_LIMIT = 3;

    /** 잎다육 카테고리 (/shop) */
    private static final List<String> LEAF_CATEGORIES = List.of(
            "에케베리아", "세덤", "하월시아", "크라슐라", "칼랑코에", "기타 잎다육"
    );

    /** 홈 잎다육 탭 (HOME_FEATURED + SUCCULENT_PLANTS) */
    private static final List<String> LEAF_CATEGORIES_FOR_HOME = List.of(
            "잎다육", "에케베리아", "세덤", "하월시아", "크라슐라", "칼랑코에", "기타 잎다육"
    );

    /** 줄기 다육 카테고리 (/shop/stem) */
    private static final List<String> STEM_CATEGORIES = List.of(
            "선인장", "유포르비아", "파키포디움", "아데니움", "기타 줄기다육"
    );

    /** 코덱스 다육 카테고리 (/shop/caudex) */
    private static final List<String> CAUDEX_CATEGORIES = List.of(
            "아데니움", "파키포디움", "디오스코레아", "포케아", "아데니아", "희귀 코덱스"
    );

    private final ProductRepository productRepository;

    /**
     * 홈 BEST SELLER (/) - 잎다육/줄기 다육/코덱스 다육 탭별 상위 3개
     */
    public List<Product> getHomeFeaturedProducts(String tab) {
        List<Product> products;
        if ("잎다육".equals(tab)) {
            products = productRepository.findByCategoryIn(LEAF_CATEGORIES_FOR_HOME);
        } else if ("줄기 다육".equals(tab)) {
            products = productRepository.findByCategoryIn(STEM_CATEGORIES);
        } else if ("코덱스 다육".equals(tab)) {
            products = productRepository.findByCategoryIn(CAUDEX_CATEGORIES);
        } else {
            products = productRepository.findByCategory(tab);
        }
        return products.stream()
                .limit(HOME_FEATURED_LIMIT)
                .toList();
    }
}
```

### 🎯 메서드의 목적

이 메서드는 메인 화면(Home)의 **"BEST SELLER" 섹션**에서 탭을 전환할 때, 각 카테고리에 맞는 추천 상품을 제공합니다.

### 🔄 동작 흐름 분석

#### Step 1️⃣ 탭에 따른 카테고리 분류

사용자가 선택한 `tab` 파라미터에 따라 조회할 카테고리 목록을 결정합니다.

```
탭 선택: "잎다육" 클릭
    ↓
LEAF_CATEGORIES_FOR_HOME 사용
    ↓
["잎다육", "에케베리아", "세덤", "하월시아", "크라슐라", "칼랑코에", "기타 잎다육"]
```

**🔹 다중 카테고리 처리**

- "잎다육", "줄기 다육", "코덱스 다육" 탭은 여러 세부 카테고리를 묶어서 조회
- 미리 정의된 상수 리스트 활용

**🔹 개별 카테고리 처리**

- 정의되지 않은 탭 이름이 들어오면 해당 문자열을 카테고리로 간주
- `findByCategory(tab)` 호출

#### Step 2️⃣ 메모리 내 데이터 제한

```java
return products.stream()
        .limit(HOME_FEATURED_LIMIT)  // 최대 3개
        .toList();
```

Repository에서 가져온 전체 상품 리스트를 Java Stream API로 가공합니다.

- `HOME_FEATURED_LIMIT = 3`: 탭별로 최대 **3개의 상품**만 반환
- 메모리 상에서 제한 → DB 쿼리는 전체 조회

### 🎨 UI/UX 관점에서 보기

```
┌─────────────────────────────────────┐
│         BEST SELLER 🏆              │
│                                      │
│  [잎다육] [줄기 다육] [코덱스 다육] │ ← 탭 메뉴
│  ─────                               │
│                                      │
│  🌱 에케베리아 '라울'     15,000원  │
│  🌱 세덤 '오로라'         8,000원   │
│  🌱 하월시아 '옵투사'     12,000원  │
│                                      │
└─────────────────────────────────────┘
```

### 💡 설계 포인트

| 포인트             | 설명                                            |
| ------------------ | ----------------------------------------------- |
| **📌 상수 활용**   | 카테고리 그룹을 상수로 관리하여 유지보수성 향상 |
| **🎯 명확한 책임** | 탭 → 카테고리 → 상품 조회의 단계가 명확함       |
| **🔄 유연성**      | 새로운 탭 추가 시 `else if` 분기만 추가하면 됨  |
| **⚡ 성능 고려**   | limit()으로 불필요한 데이터 전송 방지           |

### 🚀 개선 포인트

현재 코드는 **메모리 상에서 limit**을 적용하지만, 더 효율적으로는:

```java
// 개선안: DB 쿼리 레벨에서 제한
List<Product> findTop3ByCategoryIn(Collection<String> categories);
```

이렇게 하면 DB에서 3개만 가져오므로 네트워크 비용과 메모리 사용량이 줄어듭니다!

---
<a id="Key-Takeaways"></a>
## 🎓 핵심 요약

### findByCategoryIn

- ✨ SQL IN 절을 자동 생성하는 Spring Data JPA 쿼리 메서드
- ✨ Collection 타입 파라미터로 다중 카테고리 검색
- ✨ 코드 간결성과 타입 안정성 확보

### getHomeFeaturedProducts

- ✨ 홈 화면 탭별 추천 상품 제공
- ✨ 카테고리 그룹을 상수로 관리
- ✨ Stream API로 최대 3개 상품만 반환

---
