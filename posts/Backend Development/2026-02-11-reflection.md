---
title: 📦 Reflection 완벽 가이드
published: true
tags:
    - JPA
    - Spring Data JPA
    - Hibernate
    - BaseEntity
    - ORM
    - Backend
    - Java
    - Reflection

date: 2026-02-11
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# Reflection 완벽 가이드

> 💡 **핵심**: 실행 중에 클래스 정보를 분석하고 동적으로 객체를 생성하는 Java의 강력한 기능  
> JPA, Spring 등 대부분의 프레임워크가 내부적으로 사용하는 핵심 기술

---

## 🎯 Reflection이란?

### 정의

**리플렉션(Reflection)**
- **실행 중(Runtime)**에 클래스의 정보를 분석하고
- 필드, 메서드, 생성자에 **접근**하고
- **동적으로 객체를 생성**할 수 있는 Java 기능

### 근본적인 차이: 정적 vs 동적

```java
// 일반적인 방식: 컴파일 타임에 결정
Member member = new Member("kobe");

// 리플렉션: 런타임에 동적으로 결정
Class<?> clazz = Class.forName("com.example.Member");
Object obj = clazz.getDeclaredConstructor().newInstance();
```

**차이점**:
```
일반 방식              리플렉션
    ↓                     ↓
컴파일 시점 결정      런타임 시점 결정
타입 안정성 O         타입 안정성 X
빠른 속도             느린 속도
명확한 코드           동적인 코드
```

---

## 🔍 일반 객체 생성 vs 리플렉션

### 일반적인 객체 생성 (컴파일 타임)

```java
// 1. 클래스가 명확히 결정됨
Member member = new Member();

// 2. IDE가 자동완성 제공
member.getName();  // ← 메서드 확인 가능

// 3. 컴파일 타임에 타입 체크
member.setName("kobe");  // ← 타입 안전

// 4. 빠른 실행 속도
```

### 리플렉션을 사용한 객체 생성 (런타임)

```java
// 1. 클래스 이름을 문자열로 전달
Class<?> clazz = Class.forName("com.example.Member");

// 2. 생성자 찾기
Constructor<?> constructor = clazz.getDeclaredConstructor();

// 3. 접근 가능하게 설정 (private도 접근 가능)
constructor.setAccessible(true);

// 4. 객체 생성
Object obj = constructor.newInstance();

// 5. 타입 캐스팅 필요
Member member = (Member) obj;
```

**핵심 차이**:
```java
// 컴파일 타임: "어떤 클래스를 쓸지 이미 알고 있음"
Member member = new Member();

// 런타임: "실행하기 전까지 어떤 클래스를 쓸지 모름"
Class<?> clazz = Class.forName(className);  // className은 실행 시 결정
```

---

## 🚀 리플렉션으로 가능한 것들

### 1. 클래스 정보 조회

```java
Class<?> clazz = Member.class;

// 클래스 이름
String className = clazz.getName();  // "com.example.Member"
String simpleName = clazz.getSimpleName();  // "Member"

// 패키지 정보
Package pkg = clazz.getPackage();

// 상속 정보
Class<?> superClass = clazz.getSuperclass();
Class<?>[] interfaces = clazz.getInterfaces();

// 어노테이션 확인
boolean isEntity = clazz.isAnnotationPresent(Entity.class);
```

### 2. 필드 목록 조회

```java
@Entity
public class Member {
    private Long id;
    private String name;
    private String email;
}

// 모든 필드 조회
Field[] fields = Member.class.getDeclaredFields();

for (Field field : fields) {
    System.out.println("필드명: " + field.getName());
    System.out.println("타입: " + field.getType());
    System.out.println("접근제어자: " + Modifier.toString(field.getModifiers()));
}

// 출력:
// 필드명: id, 타입: class java.lang.Long, 접근제어자: private
// 필드명: name, 타입: class java.lang.String, 접근제어자: private
// 필드명: email, 타입: class java.lang.String, 접근제어자: private
```

### 3. private 필드 접근 및 수정

```java
public class Member {
    private String name = "Original";
    
    private Member() {
        // private 생성자
    }
}

// 리플렉션으로 private 접근
Class<?> clazz = Member.class;

// private 필드 접근
Field nameField = clazz.getDeclaredField("name");
nameField.setAccessible(true);  // private 무력화

// 객체 생성
Constructor<?> constructor = clazz.getDeclaredConstructor();
constructor.setAccessible(true);
Object member = constructor.newInstance();

// private 필드 값 읽기
String originalName = (String) nameField.get(member);
System.out.println(originalName);  // "Original"

// private 필드 값 수정
nameField.set(member, "Modified");
String newName = (String) nameField.get(member);
System.out.println(newName);  // "Modified"
```

### 4. 메서드 호출

```java
public class Member {
    private String name;
    
    private void setName(String name) {
        this.name = name;
    }
    
    private String getName() {
        return this.name;
    }
}

// 리플렉션으로 private 메서드 호출
Class<?> clazz = Member.class;
Object member = clazz.getDeclaredConstructor().newInstance();

// private 메서드 찾기
Method setNameMethod = clazz.getDeclaredMethod("setName", String.class);
setNameMethod.setAccessible(true);

// private 메서드 호출
setNameMethod.invoke(member, "kobe");

// 값 확인
Method getNameMethod = clazz.getDeclaredMethod("getName");
getNameMethod.setAccessible(true);
String name = (String) getNameMethod.invoke(member);
System.out.println(name);  // "kobe"
```

### 5. 어노테이션 읽기

```java
@Entity
@Table(name = "members")
public class Member {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 50)
    private String name;
}

// 클래스 레벨 어노테이션
Class<?> clazz = Member.class;

if (clazz.isAnnotationPresent(Entity.class)) {
    System.out.println("이 클래스는 Entity입니다");
}

Table table = clazz.getAnnotation(Table.class);
System.out.println("테이블명: " + table.name());  // "members"

// 필드 레벨 어노테이션
Field nameField = clazz.getDeclaredField("name");
Column column = nameField.getAnnotation(Column.class);
System.out.println("nullable: " + column.nullable());  // false
System.out.println("length: " + column.length());      // 50
```

---

## 🔥 리플렉션 실제 사용 사례

### 1. JPA에서의 사용 ⭐⭐⭐⭐⭐

**문제 상황**:
```java
// JPA는 DB 조회 결과를 어떻게 객체로 변환할까?
SELECT id, name, email FROM member WHERE id = 1;

// 결과:
// id=1, name="kobe", email="kobe@example.com"

// → Member 객체로 어떻게 변환?
```

**JPA 내부 동작 (단순화)**:
```java
public class SimpleJpaImplementation {
    
    public <T> T find(Class<T> entityClass, Object id) {
        // 1. DB에서 데이터 조회
        Map<String, Object> dbData = queryDatabase(entityClass, id);
        // {id=1, name="kobe", email="kobe@example.com"}
        
        try {
            // 2. 리플렉션으로 기본 생성자 호출
            Constructor<T> constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T entity = constructor.newInstance();
            
            // 3. 리플렉션으로 각 필드에 값 주입
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                
                String fieldName = field.getName();
                Object value = dbData.get(fieldName);
                
                field.set(entity, value);
            }
            
            return entity;
            
        } catch (Exception e) {
            throw new RuntimeException("Entity 생성 실패", e);
        }
    }
}

// 사용
Member member = jpa.find(Member.class, 1L);
// ↑ JPA는 Member 클래스가 어떻게 생겼는지 미리 모름
// ↑ 런타임에 리플렉션으로 확인하고 객체 생성
```

**그래서 JPA Entity에 기본 생성자가 필요한 이유**:
```java
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)  // 필수!
public class Member {
    @Id
    private Long id;
    private String name;
    
    // JPA가 리플렉션으로 이 생성자를 호출
    // protected Member() {}
}
```

### 2. Spring DI (의존성 주입)

**Spring의 내부 동작**:
```java
// 우리가 작성하는 코드
@Service
public class MemberService {
    // Spring이 자동으로 주입
}

// Spring 내부 (단순화)
public class SimpleSpringContainer {
    
    private Map<Class<?>, Object> beans = new ConcurrentHashMap<>();
    
    public void scan(String basePackage) {
        // 1. 패키지 스캔
        List<Class<?>> classes = findClassesInPackage(basePackage);
        
        for (Class<?> clazz : classes) {
            // 2. @Service, @Component 등 확인
            if (clazz.isAnnotationPresent(Service.class) ||
                clazz.isAnnotationPresent(Component.class)) {
                
                try {
                    // 3. 리플렉션으로 객체 생성
                    Constructor<?> constructor = clazz.getDeclaredConstructor();
                    Object bean = constructor.newInstance();
                    
                    // 4. 컨테이너에 등록
                    beans.put(clazz, bean);
                    
                } catch (Exception e) {
                    throw new RuntimeException("Bean 생성 실패", e);
                }
            }
        }
    }
    
    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }
}

// 사용
MemberService service = spring.getBean(MemberService.class);
```

### 3. Jackson (JSON 직렬화/역직렬화)

```java
// JSON 문자열
String json = """
    {
        "id": 1,
        "name": "kobe",
        "email": "kobe@example.com"
    }
    """;

// Jackson 내부 동작 (단순화)
public class SimpleObjectMapper {
    
    public <T> T readValue(String json, Class<T> clazz) {
        // 1. JSON 파싱
        Map<String, Object> jsonMap = parseJson(json);
        
        try {
            // 2. 리플렉션으로 객체 생성
            Constructor<T> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            T object = constructor.newInstance();
            
            // 3. 리플렉션으로 필드에 값 주입
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                
                String fieldName = field.getName();
                Object value = jsonMap.get(fieldName);
                
                // 타입 변환
                Object convertedValue = convertType(value, field.getType());
                field.set(object, convertedValue);
            }
            
            return object;
            
        } catch (Exception e) {
            throw new RuntimeException("역직렬화 실패", e);
        }
    }
}

// 사용
ObjectMapper mapper = new ObjectMapper();
Member member = mapper.readValue(json, Member.class);
```

### 4. 어노테이션 기반 프레임워크

```java
// Spring MVC Controller
@RestController
@RequestMapping("/api/members")
public class MemberController {
    
    @GetMapping("/{id}")
    public Member getMember(@PathVariable Long id) {
        return memberService.findById(id);
    }
}

// Spring 내부 동작 (단순화)
public class SimpleDispatcherServlet {
    
    public void init() {
        // 1. 모든 클래스 스캔
        List<Class<?>> classes = scanClasses();
        
        for (Class<?> clazz : classes) {
            // 2. @RestController 확인
            if (clazz.isAnnotationPresent(RestController.class)) {
                
                // 3. @RequestMapping 읽기
                RequestMapping classMapping = 
                    clazz.getAnnotation(RequestMapping.class);
                String basePath = classMapping.value()[0];
                
                // 4. 모든 메서드 확인
                for (Method method : clazz.getDeclaredMethods()) {
                    
                    // 5. @GetMapping 확인
                    if (method.isAnnotationPresent(GetMapping.class)) {
                        GetMapping getMapping = 
                            method.getAnnotation(GetMapping.class);
                        String path = basePath + getMapping.value()[0];
                        
                        // 6. URL 매핑 등록
                        registerHandler("GET", path, clazz, method);
                    }
                }
            }
        }
    }
}
```

---

## ⚖️ 리플렉션의 장단점

### ✅ 장점

1. **동적 프로그래밍 가능**
```java
// 실행 시점에 클래스 결정
String className = config.getProperty("entity.class");
Class<?> clazz = Class.forName(className);
Object entity = clazz.getDeclaredConstructor().newInstance();
```

2. **프레임워크 개발 가능**
```java
// Spring, JPA 등 범용 프레임워크 구현 가능
// 사용자가 만든 어떤 클래스든 처리 가능
```

3. **런타임에 구조 분석 가능**
```java
// 클래스 구조를 동적으로 파악
Field[] fields = clazz.getDeclaredFields();
Method[] methods = clazz.getDeclaredMethods();
```

### ❌ 단점

1. **성능 오버헤드**
```java
// 일반 방식 (빠름)
Member member = new Member();
member.setName("kobe");

// 리플렉션 (약 10~100배 느림)
Class<?> clazz = Member.class;
Constructor<?> constructor = clazz.getDeclaredConstructor();
Object obj = constructor.newInstance();
Method method = clazz.getDeclaredMethod("setName", String.class);
method.invoke(obj, "kobe");
```

**성능 비교**:
```
일반 메서드 호출:    1x
리플렉션 메서드 호출: 10~100x
```

2. **타입 안정성 낮음**
```java
// 일반 방식: 컴파일 타임 체크
member.setName(123);  // ← 컴파일 에러!

// 리플렉션: 런타임 에러
method.invoke(obj, 123);  // ← 실행 중 에러!
// IllegalArgumentException: argument type mismatch
```

3. **보안 제약**
```java
// private 필드/메서드 접근 시 SecurityManager 체크
field.setAccessible(true);  // ← 보안 정책에 따라 거부될 수 있음
```

4. **디버깅 어려움**
```java
// 일반 방식: 스택 트레이스 명확
member.getName();
// at com.example.MemberService.getMember(MemberService.java:42)

// 리플렉션: 스택 트레이스 복잡
method.invoke(obj);
// at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
// at sun.reflect.NativeMethodAccessorImpl.invoke(...)
// at sun.reflect.DelegatingMethodAccessorImpl.invoke(...)
// at java.lang.reflect.Method.invoke(Method.java:498)
```

---

## 🎯 실무 사용 가이드

### ✅ 리플렉션을 사용해도 되는 경우

1. **프레임워크/라이브러리 개발**
```java
// 범용 유틸리티 라이브러리
public class BeanMapper {
    public <T> T map(Object source, Class<T> targetClass) {
        // 리플렉션으로 범용 매핑
    }
}
```

2. **테스트 코드**
```java
@Test
public void testPrivateMethod() throws Exception {
    // private 메서드 테스트 시
    Method method = MyClass.class.getDeclaredMethod("privateMethod");
    method.setAccessible(true);
    Object result = method.invoke(instance);
    
    assertThat(result).isEqualTo(expected);
}
```

3. **플러그인 시스템**
```java
// 동적 플러그인 로딩
String pluginClassName = loadPluginConfig();
Class<?> pluginClass = Class.forName(pluginClassName);
Plugin plugin = (Plugin) pluginClass.getDeclaredConstructor().newInstance();
```

### ❌ 리플렉션을 사용하지 말아야 하는 경우

1. **일반 비즈니스 로직**
```java
// ❌ 나쁜 예
public void updateMember(Long id, String name) {
    Member member = findById(id);
    
    // 리플렉션으로 setter 호출 (불필요)
    Method setNameMethod = Member.class.getDeclaredMethod("setName", String.class);
    setNameMethod.invoke(member, name);
}

// ✅ 좋은 예
public void updateMember(Long id, String name) {
    Member member = findById(id);
    member.setName(name);  // 직접 호출
}
```

2. **성능이 중요한 코드**
```java
// ❌ 성능 저하
for (int i = 0; i < 1_000_000; i++) {
    method.invoke(obj);  // 매우 느림
}

// ✅ 직접 호출
for (int i = 0; i < 1_000_000; i++) {
    obj.doSomething();  // 빠름
}
```

3. **타입 안정성이 중요한 경우**
```java
// ❌ 타입 안정성 없음
method.invoke(obj, wrongTypeArgument);  // 런타임 에러

// ✅ 타입 안정성 보장
obj.method(correctTypeArgument);  // 컴파일 타임 체크
```

---

## 🔬 리플렉션 최적화 기법

### 1. 리플렉션 결과 캐싱

```java
// ❌ 나쁜 예: 매번 리플렉션
public void badExample() {
    for (int i = 0; i < 1000; i++) {
        Method method = clazz.getDeclaredMethod("getName");  // 매번 조회
        method.invoke(obj);
    }
}

// ✅ 좋은 예: 한 번만 조회하고 재사용
public class ReflectionCache {
    private static final Map<String, Method> METHOD_CACHE = new ConcurrentHashMap<>();
    
    public Method getMethod(Class<?> clazz, String methodName) {
        String key = clazz.getName() + "#" + methodName;
        
        return METHOD_CACHE.computeIfAbsent(key, k -> {
            try {
                Method method = clazz.getDeclaredMethod(methodName);
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
```

### 2. MethodHandle 사용 (Java 7+)

```java
// 리플렉션보다 빠른 대안
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class MethodHandleExample {
    
    public void example() throws Throwable {
        // MethodHandle 생성 (한 번만)
        MethodHandles.Lookup lookup = MethodHandles.lookup();
        MethodHandle mh = lookup.findVirtual(
            Member.class,
            "getName",
            MethodType.methodType(String.class)
        );
        
        Member member = new Member("kobe");
        
        // 호출 (리플렉션보다 빠름)
        String name = (String) mh.invoke(member);
    }
}

// 성능 비교:
// 일반 호출:      1x
// MethodHandle:   2~3x
// Reflection:     10~100x
```

---

## 📋 핵심 요약

### 리플렉션의 핵심 개념

| 구분 | 설명 | 예시 |
|------|------|------|
| **정의** | 런타임에 클래스 정보 분석 및 조작 | Class.forName() |
| **시점** | 실행 중(Runtime) | 컴파일 타임 X |
| **용도** | 동적 객체 생성, 프레임워크 개발 | JPA, Spring |
| **단점** | 성능 저하, 타입 안정성 낮음 | 10~100배 느림 |

### 일반 방식 vs 리플렉션

| 항목 | 일반 방식 | 리플렉션 |
|------|----------|---------|
| **결정 시점** | 컴파일 타임 | 런타임 |
| **타입 안정성** | 높음 | 낮음 |
| **성능** | 빠름 (1x) | 느림 (10~100x) |
| **가독성** | 좋음 | 나쁨 |
| **사용 목적** | 일반 로직 | 프레임워크 개발 |

### 주요 사용 사례

```
1. JPA → Entity 동적 생성 및 필드 주입
2. Spring DI → Bean 자동 생성 및 주입
3. Jackson → JSON ↔ 객체 변환
4. Spring MVC → 어노테이션 기반 라우팅
```

---

## 💭 자주 묻는 질문 (FAQ)

### Q1. 왜 JPA Entity에 기본 생성자가 필요한가?

```java
// JPA가 리플렉션으로 객체 생성
Constructor<?> constructor = entityClass.getDeclaredConstructor();
Object entity = constructor.newInstance();

// 기본 생성자가 없으면?
// NoSuchMethodException 발생!
```

### Q2. private 필드에 어떻게 접근하나?

```java
Field field = clazz.getDeclaredField("privateField");
field.setAccessible(true);  // private 무력화

Object value = field.get(instance);  // 읽기
field.set(instance, newValue);       // 쓰기
```

### Q3. 리플렉션은 왜 느린가?

**이유**:
1. 메서드/필드 탐색 오버헤드
2. 보안 체크
3. 타입 체크 및 변환
4. JVM 최적화 불가

```java
// 일반 호출: JVM이 최적화 가능
member.getName();  // 인라인화, 컴파일 최적화

// 리플렉션: 최적화 불가능
method.invoke(member);  // 동적 호출, 최적화 어려움
```

### Q4. 언제 리플렉션을 써야 하나?

**사용해야 할 때**:
- 프레임워크/라이브러리 개발
- 플러그인 시스템
- 범용 유틸리티

**사용하지 말아야 할 때**:
- 일반 비즈니스 로직
- 성능이 중요한 코드
- 타입 안정성이 중요한 경우

---

## 🎁 보너스: 실전 예제

### JPA Entity 동적 생성기

```java
public class EntityCreator {
    
    public <T> T createEntity(Class<T> entityClass, Map<String, Object> data) {
        try {
            // 1. 기본 생성자로 객체 생성
            Constructor<T> constructor = entityClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            T entity = constructor.newInstance();
            
            // 2. 모든 필드에 값 주입
            for (Field field : entityClass.getDeclaredFields()) {
                field.setAccessible(true);
                
                String fieldName = field.getName();
                Object value = data.get(fieldName);
                
                if (value != null) {
                    // 타입 변환
                    Object convertedValue = convertType(value, field.getType());
                    field.set(entity, convertedValue);
                }
            }
            
            return entity;
            
        } catch (Exception e) {
            throw new RuntimeException("Entity 생성 실패: " + entityClass.getName(), e);
        }
    }
    
    private Object convertType(Object value, Class<?> targetType) {
        if (targetType == Long.class && value instanceof Integer) {
            return ((Integer) value).longValue();
        }
        // ... 기타 타입 변환
        return value;
    }
}

// 사용
Map<String, Object> data = Map.of(
    "id", 1L,
    "name", "kobe",
    "email", "kobe@example.com"
);

Member member = entityCreator.createEntity(Member.class, data);
```

### Spring 간단 DI 컨테이너

```java
public class SimpleDIContainer {
    
    private final Map<Class<?>, Object> beans = new ConcurrentHashMap<>();
    
    public void register(Class<?> clazz) {
        try {
            // 1. 생성자 확인
            Constructor<?>[] constructors = clazz.getDeclaredConstructors();
            Constructor<?> constructor = constructors[0];
            
            // 2. 생성자 파라미터 확인
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] params = new Object[paramTypes.length];
            
            // 3. 의존성 주입
            for (int i = 0; i < paramTypes.length; i++) {
                params[i] = getBean(paramTypes[i]);
            }
            
            // 4. 객체 생성
            Object bean = constructor.newInstance(params);
            
            // 5. 컨테이너에 등록
            beans.put(clazz, bean);
            
        } catch (Exception e) {
            throw new RuntimeException("Bean 등록 실패: " + clazz.getName(), e);
        }
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> clazz) {
        return (T) beans.get(clazz);
    }
}

// 사용
SimpleDIContainer container = new SimpleDIContainer();
container.register(MemberRepository.class);
container.register(MemberService.class);  // MemberRepository 자동 주입

MemberService service = container.getBean(MemberService.class);
```

---

## 🎯 마지막 체크포인트

**다음 질문에 답할 수 있다면 이해 완료**:

- [ ] 리플렉션이 무엇인지 설명할 수 있는가?
- [ ] 일반 객체 생성과 리플렉션의 차이를 아는가?
- [ ] JPA가 리플렉션을 사용하는 이유를 설명할 수 있는가?
- [ ] 리플렉션의 장단점을 나열할 수 있는가?
- [ ] 언제 리플렉션을 사용하고, 언제 사용하지 말아야 하는지 판단할 수 있는가?

**Remember**:
```
Reflection = 런타임에 클래스 정보 분석 및 동적 객체 생성
JPA, Spring 등 프레임워크의 핵심 기술
일반 코드보다 10~100배 느림 → 비즈니스 로직에서는 사용 지양
프레임워크 개발, 테스트 코드에서만 사용
```
