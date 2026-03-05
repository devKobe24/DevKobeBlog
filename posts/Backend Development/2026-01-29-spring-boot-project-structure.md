---
title: 🏗️ Spring Boot 프로젝트 구조 완벽 가이드
published: true
tags:
  - Backend Development
  - Server
  - Java
  - Spring Boot

date: 2026-01-29
thumbnail: /assets/img/thumbnail/BackendDevelopment.jpg
---

# 🏗️ Spring Boot 프로젝트 구조 완벽 가이드

> 초보에서 실무자로! 유지보수 가능한 프로젝트 구조 설계의 모든 것

---

## 📋 목차

1. [왜 프로젝트 구조가 중요한가?](#Why-Is-Project-Structure-Important)
2. [계층형 구조 (Layered Architecture)](#Layered-Architecture)
3. [도메인 중심 구조 (Package by Feature)](#Domain-Centric-Structure-package-by-feature)
4. [계층 + 도메인 혼합 구조](#Hybrid-Structure-Layered-Domain-Based)
5. [DDD 스타일 (Domain Driven Design)](#DDD-Style-Domain-Driven-Design)
6. [헥사고날/클린 아키텍처](#Hexagonal-Clean-Architecture)
7. [패턴 비교와 선택 가이드](#Pattern-Comparison-and-Selection-Guide)
8. [필수 공통 패키지 구성](#Essential-Shared-Package-Structure)
9. [마이그레이션 전략](#Migration-Strategy)
10. [핵심 요약](#Key-Takeaways)

---
<a id="Why-Is-Project-Structure-Important"></a>
## 1️⃣ 왜 프로젝트 구조가 중요한가?

### 💡 프로젝트 구조가 결정하는 것들

```
좋은 구조 ✅                     나쁜 구조 ❌
    ↓                               ↓
빠른 기능 추가                   기능 하나 추가에 하루
쉬운 버그 수정                   버그 수정이 새 버그 양산
명확한 코드 위치                 "이 코드 어디 있지?" 반복
원활한 팀 협업                   충돌과 중복 코드 난무
장기적 확장성                    리팩토링 지옥
```

### 🎯 구조 설계의 핵심 원칙

| 원칙 | 설명 | 예시 |
|:---:|---|---|
| **응집도(Cohesion)** | 관련 있는 것끼리 모으기 | User 관련 코드는 user 패키지에 |
| **결합도(Coupling)** | 의존성 최소화 | Controller가 Repository 직접 참조 ❌ |
| **단일 책임** | 하나의 클래스는 하나의 이유로만 변경 | UserService는 사용자 로직만 |
| **계층 분리** | 각 계층의 역할 명확히 | Presentation ↔ Business ↔ Data |

---
<a id="Layered-Architecture"></a>
## 2️⃣ 계층형 구조 (Layered Architecture)

### 📌 개념

**역할에 따라 패키지를 나누는 가장 기본적이고 직관적인 구조**

```
Controller (요청/응답) 
    ↓
Service (비즈니스 로직)
    ↓
Repository (데이터 접근)
    ↓
Domain (엔티티)
```

---

### 📁 디렉토리 구조

```
src/main/java/com/devkobe/blog/
│
├─ 📁 controller/
│   ├─ PostController.java
│   ├─ CommentController.java
│   └─ UserController.java
│
├─ 📁 service/
│   ├─ PostService.java
│   ├─ CommentService.java
│   └─ UserService.java
│
├─ 📁 repository/
│   ├─ PostRepository.java
│   ├─ CommentRepository.java
│   └─ UserRepository.java
│
├─ 📁 domain/
│   ├─ Post.java
│   ├─ Comment.java
│   └─ User.java
│
└─ 📁 dto/
    ├─ PostRequest.java
    ├─ PostResponse.java
    └─ CommentRequest.java
```

---

### 💻 실제 코드 예시

#### Controller 계층

```java
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final PostService postService;
    
    @GetMapping
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        return ResponseEntity.ok(postService.createPost(request));
    }
}
```

#### Service 계층

```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    
    private final PostRepository postRepository;
    
    public List<PostResponse> getAllPosts() {
        return postRepository.findAll().stream()
            .map(PostResponse::from)
            .toList();
    }
    
    @Transactional
    public PostResponse createPost(PostRequest request) {
        Post post = Post.create(request);
        Post saved = postRepository.save(post);
        return PostResponse.from(saved);
    }
}
```

#### Repository 계층

```java
public interface PostRepository extends JpaRepository<Post, Long> {
    
    List<Post> findByTitleContaining(String keyword);
    
    @Query("SELECT p FROM Post p WHERE p.author = :author ORDER BY p.createdAt DESC")
    List<Post> findByAuthorOrderByCreatedAtDesc(@Param("author") String author);
}
```

#### Domain 계층

```java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTimeEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    private String author;
    
    public static Post create(PostRequest request) {
        Post post = new Post();
        post.title = request.getTitle();
        post.content = request.getContent();
        post.author = request.getAuthor();
        return post;
    }
}
```

---

### 📊 장단점 분석

#### ✅ 장점

| 장점 | 설명 |
|:---:|---|
| 🎯 **직관적** | 누구나 쉽게 이해 가능 |
| 📚 **학습 용이** | Spring 공식 가이드와 일치 |
| 🚀 **빠른 시작** | 고민 없이 바로 개발 시작 |
| 👥 **낮은 진입장벽** | 신규 팀원 온보딩 쉬움 |
| 📖 **풍부한 레퍼런스** | 대부분의 튜토리얼이 이 구조 |

#### ❌ 단점

| 단점 | 설명 |
|:---:|---|
| 📈 **확장성 문제** | 도메인 10개 넘으면 패키지 비대 |
| 🔀 **낮은 응집도** | 기능 하나 수정 시 여러 패키지 이동 |
| 🔍 **파일 찾기 어려움** | Controller에 20개 파일이 섞여있음 |
| 🧩 **모듈화 어려움** | 기능 단위 분리가 힘듦 |

---

### 🎯 적용 시나리오

```
✅ 적합한 경우:
- 첫 Spring Boot 프로젝트
- 개인 프로젝트, 토이 프로젝트
- 5개 미만의 도메인
- 학습용 프로젝트
- 빠른 프로토타이핑

❌ 부적합한 경우:
- 10개 이상의 도메인
- 장기 운영 예정 서비스
- 팀 규모 5명 이상
- MSA 전환 가능성
```

---
<a id="Domain-Centric-Structure-package-by-feature"></a>
## 3️⃣ 도메인 중심 구조 (Package by Feature)

### 📌 개념

**기능(도메인) 단위로 패키지를 나누는 실무 최강 구조**

```
각 도메인이 독립적인 모듈처럼 동작
post 패키지만 보면 게시글 기능 전체 파악 가능
```

---

### 📁 디렉토리 구조

```
src/main/java/com/devkobe/blog/
│
├─ 📁 post/
│   ├─ PostController.java
│   ├─ PostService.java
│   ├─ PostRepository.java
│   ├─ Post.java
│   └─ 📁 dto/
│       ├─ PostRequest.java
│       └─ PostResponse.java
│
├─ 📁 comment/
│   ├─ CommentController.java
│   ├─ CommentService.java
│   ├─ CommentRepository.java
│   ├─ Comment.java
│   └─ 📁 dto/
│       ├─ CommentRequest.java
│       └─ CommentResponse.java
│
├─ 📁 user/
│   ├─ UserController.java
│   ├─ UserService.java
│   ├─ UserRepository.java
│   ├─ User.java
│   └─ 📁 dto/
│
├─ 📁 tag/
│   ├─ TagController.java
│   ├─ TagService.java
│   ├─ TagRepository.java
│   └─ Tag.java
│
└─ 📁 global/
    ├─ 📁 config/
    │   ├─ SecurityConfig.java
    │   ├─ JpaConfig.java
    │   └─ WebConfig.java
    │
    ├─ 📁 error/
    │   ├─ GlobalExceptionHandler.java
    │   ├─ ErrorCode.java
    │   └─ ErrorResponse.java
    │
    ├─ 📁 common/
    │   ├─ BaseTimeEntity.java
    │   └─ ApiResponse.java
    │
    └─ 📁 util/
        ├─ JwtUtil.java
        └─ DateUtil.java
```

---

### 💻 실제 구조 예시

#### Post 도메인 (완전한 기능 단위)

```java
// post/PostController.java
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;
    // 게시글 관련 엔드포인트만
}

// post/PostService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final TagService tagService;  // 다른 도메인 참조
    // 게시글 비즈니스 로직만
}

// post/PostRepository.java
public interface PostRepository extends JpaRepository<Post, Long> {
    // 게시글 쿼리만
}

// post/Post.java
@Entity
public class Post extends BaseTimeEntity {
    // 게시글 엔티티
}

// post/dto/PostRequest.java
// post/dto/PostResponse.java
```

---

### 📊 장단점 분석

#### ✅ 장점

| 장점 | 설명 | 예시 |
|:---:|---|---|
| 🎯 **높은 응집도** | 관련 코드가 한 곳에 | post 패키지만 보면 게시글 기능 전체 파악 |
| 🔧 **쉬운 유지보수** | 기능 수정 시 한 패키지만 열면 됨 | 게시글 수정 → post 패키지만 |
| 🧩 **모듈화 용이** | 기능 단위로 독립적 | post 기능을 통째로 다른 프로젝트로 이동 가능 |
| 👥 **팀 협업** | 도메인별 담당자 배정 쉬움 | A는 post, B는 user 담당 |
| 📈 **확장성** | 도메인 추가해도 구조 유지 | 100개 도메인도 OK |

#### ❌ 단점

| 단점 | 설명 |
|:---:|---|
| 🆕 **학습 곡선** | 처음엔 낯설 수 있음 |
| 🔀 **도메인 간 의존성** | 순환 참조 주의 필요 |
| 📚 **초기 설계 중요** | 도메인 경계 설정이 핵심 |

---

### 🎯 적용 시나리오

```
✅ 적합한 경우:
- 중규모 이상 서비스 (도메인 5개 이상)
- 장기 운영 예정
- 팀 규모 3명 이상
- MSA 전환 고려
- 기능 단위 개발/배포

❌ 부적합한 경우:
- 첫 Spring Boot 프로젝트 (학습 부담)
- 도메인이 2-3개뿐인 소규모
- 1-2주 단기 프로토타입
```

---

### 💡 도메인 경계 설정 가이드

#### 좋은 도메인 분리 예시

```
✅ 명확한 경계:
- post (게시글)
- comment (댓글)
- user (사용자)
- tag (태그)
- notification (알림)
```

#### 나쁜 도메인 분리 예시

```
❌ 모호한 경계:
- data (뭐든지 넣음)
- common (공통 로직 전부)
- util (유틸 전부)
- temp (임시 코드)
```

---
<a id="Hybrid-Structure-Layered-Domain-Based"></a>
## 4️⃣ 계층 + 도메인 혼합 구조

### 📌 개념

**"계층형은 단순하지만 확장이 어렵고, 도메인은 좋은데 초반엔 부담스럽다"**

→ 두 장점을 결합한 하이브리드 구조

---

### 📁 디렉토리 구조

```
src/main/java/com/devkobe/blog/
│
├─ 📁 domain/              # 핵심 도메인 로직
│   ├─ 📁 post/
│   │   ├─ Post.java
│   │   ├─ PostRepository.java (인터페이스만)
│   │   └─ PostValidator.java
│   │
│   └─ 📁 user/
│       ├─ User.java
│       ├─ UserRepository.java
│       └─ UserValidator.java
│
├─ 📁 application/         # 애플리케이션 서비스
│   ├─ 📁 post/
│   │   ├─ PostService.java
│   │   ├─ PostQueryService.java
│   │   └─ dto/
│   │
│   └─ 📁 user/
│       ├─ UserService.java
│       └─ dto/
│
├─ 📁 infrastructure/      # 기술 구현
│   ├─ 📁 persistence/
│   │   ├─ PostRepositoryImpl.java
│   │   └─ UserRepositoryImpl.java
│   │
│   └─ 📁 external/
│       ├─ EmailClient.java
│       └─ S3Client.java
│
├─ 📁 presentation/        # API 레이어
│   └─ 📁 api/
│       ├─ PostController.java
│       └─ UserController.java
│
└─ 📁 global/
    ├─ config/
    ├─ error/
    └─ common/
```

---

### 💻 실제 코드 흐름

```
[Client Request]
        ↓
PostController (presentation)
        ↓
PostService (application)
        ↓
Post / PostRepository (domain)
        ↓
PostRepositoryImpl (infrastructure)
        ↓
[Database]
```

---

### 📊 장단점 분석

#### ✅ 장점

| 장점 | 설명 |
|:---:|---|
| 🎯 **점진적 전환** | 계층형에서 DDD로 자연스럽게 이동 |
| 🔧 **유연성** | 필요한 부분만 도메인 중심으로 |
| 📚 **학습 효과** | DDD 개념을 단계적으로 습득 |
| 🧩 **명확한 역할** | 각 레이어의 책임이 명확 |

#### ❌ 단점

| 단점 | 설명 |
|:---:|---|
| 🔀 **복잡도** | 계층도 있고 도메인도 있음 |
| 📁 **깊은 디렉토리** | 파일 위치 찾기가 번거로움 |
| 🤔 **판단 필요** | "이 코드는 어디에?" 고민 증가 |

---

### 🎯 적용 시나리오

```
✅ 적합한 경우:
- 계층형에서 도메인 중심으로 전환 중
- DDD를 점진적으로 도입하고 싶을 때
- 팀 내 DDD 학습 단계
- 복잡한 비즈니스 로직이 일부만 있음

❌ 부적합한 경우:
- 명확한 DDD가 필요한 경우
- 단순한 CRUD만 있는 경우
- 팀원 모두 DDD에 익숙한 경우
```

---
<a id="DDD-Style-Domain-Driven-Design"></a>
## 5️⃣ DDD 스타일 (Domain Driven Design)

### 📌 개념

**비즈니스 도메인 중심으로 설계, 기술은 부차적**

핵심: 도메인 모델이 주인, 프레임워크/DB는 도구

---

### 📁 디렉토리 구조

```
src/main/java/com/devkobe/blog/
│
├─ 📁 post/                    # Bounded Context
│   │
│   ├─ 📁 domain/              # 도메인 계층 (핵심!)
│   │   ├─ Post.java           # Aggregate Root
│   │   ├─ Comment.java        # Entity
│   │   ├─ Tag.java            # Value Object
│   │   ├─ PostRepository.java # Repository 인터페이스
│   │   ├─ PostService.java    # Domain Service
│   │   └─ 📁 event/
│   │       └─ PostCreatedEvent.java
│   │
│   ├─ 📁 application/         # 애플리케이션 계층
│   │   ├─ PostCommandService.java  # Command (CUD)
│   │   ├─ PostQueryService.java    # Query (R)
│   │   └─ 📁 dto/
│   │       ├─ CreatePostCommand.java
│   │       └─ PostDto.java
│   │
│   ├─ 📁 infrastructure/      # 인프라 계층
│   │   ├─ PostRepositoryImpl.java
│   │   ├─ PostEventPublisher.java
│   │   └─ 📁 jpa/
│   │       └─ PostJpaEntity.java
│   │
│   └─ 📁 presentation/        # 표현 계층
│       ├─ PostController.java
│       └─ 📁 dto/
│           ├─ PostRequest.java
│           └─ PostResponse.java
│
├─ 📁 user/                    # 또 다른 Bounded Context
│   ├─ domain/
│   ├─ application/
│   ├─ infrastructure/
│   └─ presentation/
│
└─ 📁 shared/                  # 공유 커널
    ├─ 📁 domain/
    │   ├─ BaseEntity.java
    │   └─ DomainEvent.java
    │
    └─ 📁 infrastructure/
        └─ config/
```

---

### 💻 핵심 구성 요소

#### 1️⃣ Aggregate Root (집합체 루트)

```java
// post/domain/Post.java
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {  // Aggregate Root
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Embedded
    private Title title;  // Value Object
    
    @Embedded
    private Content content;  // Value Object
    
    @Embedded
    private Author author;  // Value Object
    
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();  // Entity
    
    @ElementCollection
    private List<Tag> tags = new ArrayList<>();  // Value Object
    
    // 비즈니스 로직 (도메인의 핵심!)
    public void publish() {
        if (this.title.isEmpty()) {
            throw new IllegalStateException("제목이 없는 게시글은 발행할 수 없습니다.");
        }
        this.status = PostStatus.PUBLISHED;
        this.publishedAt = LocalDateTime.now();
        
        // 도메인 이벤트 발행
        registerEvent(new PostPublishedEvent(this.id));
    }
    
    public void addComment(String content, String author) {
        Comment comment = Comment.create(this, content, author);
        this.comments.add(comment);
    }
    
    public void addTag(String tagName) {
        Tag tag = new Tag(tagName);
        if (this.tags.size() >= 5) {
            throw new IllegalStateException("태그는 5개까지만 추가 가능합니다.");
        }
        this.tags.add(tag);
    }
}
```

#### 2️⃣ Value Object (값 객체)

```java
// post/domain/Title.java
@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Title {
    
    @Column(nullable = false, length = 200)
    private String value;
    
    public Title(String value) {
        validate(value);
        this.value = value;
    }
    
    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("제목은 필수입니다.");
        }
        if (value.length() > 200) {
            throw new IllegalArgumentException("제목은 200자를 초과할 수 없습니다.");
        }
    }
    
    public boolean isEmpty() {
        return value == null || value.isBlank();
    }
}
```

#### 3️⃣ Repository 인터페이스 (도메인 계층)

```java
// post/domain/PostRepository.java
public interface PostRepository {
    
    Post save(Post post);
    
    Optional<Post> findById(Long id);
    
    List<Post> findByAuthor(Author author);
    
    void delete(Post post);
}
```

#### 4️⃣ Repository 구현 (인프라 계층)

```java
// post/infrastructure/PostRepositoryImpl.java
@Repository
@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepository {
    
    private final PostJpaRepository postJpaRepository;
    
    @Override
    public Post save(Post post) {
        return postJpaRepository.save(post);
    }
    
    @Override
    public Optional<Post> findById(Long id) {
        return postJpaRepository.findById(id);
    }
    
    @Override
    public List<Post> findByAuthor(Author author) {
        return postJpaRepository.findByAuthor(author);
    }
    
    @Override
    public void delete(Post post) {
        postJpaRepository.delete(post);
    }
}
```

#### 5️⃣ Application Service (CQRS 패턴)

```java
// post/application/PostCommandService.java
@Service
@RequiredArgsConstructor
@Transactional
public class PostCommandService {
    
    private final PostRepository postRepository;
    private final ApplicationEventPublisher eventPublisher;
    
    public Long createPost(CreatePostCommand command) {
        // 도메인 객체 생성
        Post post = Post.create(
            new Title(command.getTitle()),
            new Content(command.getContent()),
            new Author(command.getAuthor())
        );
        
        // 저장
        Post saved = postRepository.save(post);
        
        // 이벤트 발행
        eventPublisher.publishEvent(new PostCreatedEvent(saved.getId()));
        
        return saved.getId();
    }
    
    public void publishPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));
        
        // 비즈니스 로직은 도메인에!
        post.publish();
    }
}

// post/application/PostQueryService.java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostQueryService {
    
    private final PostRepository postRepository;
    
    public PostDto getPost(Long postId) {
        Post post = postRepository.findById(postId)
            .orElseThrow(() -> new PostNotFoundException(postId));
        
        return PostDto.from(post);
    }
    
    public List<PostDto> getPostsByAuthor(String author) {
        List<Post> posts = postRepository.findByAuthor(new Author(author));
        return posts.stream()
            .map(PostDto::from)
            .toList();
    }
}
```

---

### 📊 장단점 분석

#### ✅ 장점

| 장점 | 설명 |
|:---:|---|
| 💼 **비즈니스 중심** | 도메인 전문가와 소통 쉬움 |
| 🧪 **테스트 용이** | 도메인 로직만 독립 테스트 |
| 🔧 **유지보수** | 비즈니스 규칙 변경 시 도메인만 수정 |
| 🏗️ **확장성** | 복잡한 비즈니스에 강함 |
| 🔄 **기술 독립** | DB/프레임워크 교체 쉬움 |

#### ❌ 단점

| 단점 | 설명 |
|:---:|---|
| 📚 **학습 곡선** | DDD 개념 학습 필요 |
| ⏰ **초기 시간** | 도메인 분석에 시간 투자 |
| 🔀 **복잡도** | 단순 CRUD에는 과함 |
| 👥 **팀 합의** | 전체 팀이 DDD 이해 필요 |

---

### 🎯 적용 시나리오

```
✅ 적합한 경우:
- 복잡한 비즈니스 로직
- 장기 운영 (3년 이상)
- 도메인 전문가와 긴밀한 협업
- 높은 품질 요구
- MSA 아키텍처

❌ 부적합한 경우:
- 단순 CRUD
- 빠른 프로토타입
- 도메인이 명확하지 않음
- 팀원 DDD 경험 부족
- 단기 프로젝트 (6개월 미만)
```

---
<a id="Hexagonal-Clean-Architecture"></a>
## 6️⃣ 헥사고날/클린 아키텍처

### 📌 개념

**핵심 비즈니스 로직을 외부 기술(DB, 프레임워크, UI)로부터 완전히 분리**

```
외부 세계 (Adapter)
    ↓
포트 (Interface)
    ↓
핵심 도메인 (UseCase + Entity)
    ↓
포트 (Interface)
    ↓
외부 세계 (Adapter)
```

---

### 📁 디렉토리 구조 (클린 아키텍처)

```
src/main/java/com/devkobe/blog/
│
├─ 📁 core/                    # 핵심 계층 (의존성 없음!)
│   │
│   ├─ 📁 domain/              # 엔티티
│   │   ├─ Post.java
│   │   ├─ Comment.java
│   │   └─ User.java
│   │
│   ├─ 📁 usecase/             # 유스케이스 (비즈니스 로직)
│   │   ├─ 📁 post/
│   │   │   ├─ CreatePostUseCase.java
│   │   │   ├─ PublishPostUseCase.java
│   │   │   └─ GetPostUseCase.java
│   │   │
│   │   └─ 📁 user/
│   │       ├─ RegisterUserUseCase.java
│   │       └─ GetUserUseCase.java
│   │
│   └─ 📁 port/                # 포트 (인터페이스)
│       ├─ 📁 in/              # Input Port (UseCase Interface)
│       │   ├─ CreatePostPort.java
│       │   └─ GetPostPort.java
│       │
│       └─ 📁 out/             # Output Port (Repository Interface)
│           ├─ SavePostPort.java
│           ├─ LoadPostPort.java
│           └─ SendEmailPort.java
│
├─ 📁 adapter/                 # 어댑터 계층 (외부 세계)
│   │
│   ├─ 📁 in/                  # Driving Adapter (외부 → 내부)
│   │   ├─ 📁 web/
│   │   │   ├─ PostController.java
│   │   │   ├─ UserController.java
│   │   │   └─ 📁 dto/
│   │   │
│   │   └─ 📁 scheduler/
│   │       └─ PostScheduler.java
│   │
│   └─ 📁 out/                 # Driven Adapter (내부 → 외부)
│       ├─ 📁 persistence/
│       │   ├─ PostPersistenceAdapter.java
│       │   ├─ UserPersistenceAdapter.java
│       │   └─ 📁 jpa/
│       │       ├─ PostJpaEntity.java
│       │       └─ PostJpaRepository.java
│       │
│       ├─ 📁 email/
│       │   └─ EmailAdapter.java
│       │
│       └─ 📁 external/
│           └─ S3Adapter.java
│
└─ 📁 config/                  # 설정
    ├─ BeanConfiguration.java
    └─ SecurityConfiguration.java
```

---

### 💻 핵심 구성 요소

#### 1️⃣ Domain Entity (핵심 도메인)

```java
// core/domain/Post.java
@Getter
public class Post {
    
    private final Long id;
    private String title;
    private String content;
    private String author;
    private PostStatus status;
    
    // 생성자, 비즈니스 메서드
    public void publish() {
        if (this.title == null || this.title.isBlank()) {
            throw new IllegalStateException("제목이 없는 게시글은 발행할 수 없습니다.");
        }
        this.status = PostStatus.PUBLISHED;
    }
}
```

> 🔑 **핵심**: JPA 애노테이션이 없음! 순수 Java 객체

---

#### 2️⃣ UseCase (비즈니스 로직)

```java
// core/usecase/post/CreatePostUseCase.java
@RequiredArgsConstructor
public class CreatePostUseCase implements CreatePostPort {
    
    private final SavePostPort savePostPort;
    private final SendEmailPort sendEmailPort;
    
    @Override
    public Long execute(CreatePostCommand command) {
        // 1. 도메인 객체 생성
        Post post = new Post(
            command.getTitle(),
            command.getContent(),
            command.getAuthor()
        );
        
        // 2. 저장 (Port를 통해)
        Post saved = savePostPort.save(post);
        
        // 3. 이메일 발송 (Port를 통해)
        sendEmailPort.send(
            command.getAuthor(),
            "게시글이 생성되었습니다."
        );
        
        return saved.getId();
    }
}
```

---

#### 3️⃣ Input Port (유스케이스 인터페이스)

```java
// core/port/in/CreatePostPort.java
public interface CreatePostPort {
    Long execute(CreatePostCommand command);
}

// core/port/in/CreatePostCommand.java
@Getter
@AllArgsConstructor
public class CreatePostCommand {
    private final String title;
    private final String content;
    private final String author;
}
```

---

#### 4️⃣ Output Port (외부 인터페이스)

```java
// core/port/out/SavePostPort.java
public interface SavePostPort {
    Post save(Post post);
}

// core/port/out/LoadPostPort.java
public interface LoadPostPort {
    Optional<Post> loadById(Long id);
    List<Post> loadAll();
}

// core/port/out/SendEmailPort.java
public interface SendEmailPort {
    void send(String to, String message);
}
```

---

#### 5️⃣ Web Adapter (Controller)

```java
// adapter/in/web/PostController.java
@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {
    
    private final CreatePostPort createPostPort;
    private final GetPostPort getPostPort;
    
    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody PostRequest request) {
        // DTO → Command 변환
        CreatePostCommand command = new CreatePostCommand(
            request.getTitle(),
            request.getContent(),
            request.getAuthor()
        );
        
        // UseCase 실행
        Long postId = createPostPort.execute(command);
        
        return ResponseEntity.ok(new PostResponse(postId));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPost(@PathVariable Long id) {
        PostDto post = getPostPort.execute(id);
        return ResponseEntity.ok(PostResponse.from(post));
    }
}
```

---

#### 6️⃣ Persistence Adapter (Repository 구현)

```java
// adapter/out/persistence/PostPersistenceAdapter.java
@Component
@RequiredArgsConstructor
public class PostPersistenceAdapter implements SavePostPort, LoadPostPort {
    
    private final PostJpaRepository postJpaRepository;
    private final PostMapper postMapper;
    
    @Override
    public Post save(Post post) {
        // Domain → JPA Entity 변환
        PostJpaEntity entity = postMapper.toJpaEntity(post);
        
        // 저장
        PostJpaEntity saved = postJpaRepository.save(entity);
        
        // JPA Entity → Domain 변환
        return postMapper.toDomain(saved);
    }
    
    @Override
    public Optional<Post> loadById(Long id) {
        return postJpaRepository.findById(id)
            .map(postMapper::toDomain);
    }
    
    @Override
    public List<Post> loadAll() {
        return postJpaRepository.findAll().stream()
            .map(postMapper::toDomain)
            .toList();
    }
}
```

---

#### 7️⃣ JPA Entity (인프라 세부 구현)

```java
// adapter/out/persistence/jpa/PostJpaEntity.java
@Entity
@Table(name = "posts")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostJpaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String title;
    
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;
    
    private String author;
    
    @Enumerated(EnumType.STRING)
    private PostStatus status;
    
    // JPA 전용 로직
}
```

---

### 📊 의존성 방향

```
[Adapter In (Web)]
        ↓ 의존
    [Port In]
        ↓ 구현
    [UseCase]
        ↓ 의존
    [Port Out]
        ↑ 구현
[Adapter Out (Persistence)]
```

**핵심**: 모든 의존성이 **안쪽(Core)을 향함**

---

### 📊 장단점 분석

#### ✅ 장점

| 장점 | 설명 |
|:---:|---|
| 🔧 **기술 독립** | DB를 MySQL → PostgreSQL 교체 쉬움 |
| 🧪 **테스트 최강** | Mock Port만으로 완벽한 단위 테스트 |
| 🔄 **유연성** | UI/DB/프레임워크 변경 영향 최소 |
| 🏗️ **확장성** | 새 Adapter 추가 쉬움 |
| 📖 **명확한 경계** | 비즈니스 로직과 기술 명확히 분리 |

#### ❌ 단점

| 단점 | 설명 |
|:---:|---|
| 🔀 **복잡도** | 초기 구조 설계 복잡 |
| ⏰ **개발 시간** | Mapper, Adapter 작성 필요 |
| 📚 **학습 곡선** | 개념 이해와 적용 어려움 |
| 🔧 **보일러플레이트** | 코드량 증가 |
| 🎯 **오버엔지니어링 위험** | 단순 CRUD에는 과함 |

---

### 🎯 적용 시나리오

```
✅ 적합한 경우:
- 장기 운영 (5년 이상)
- 기술 스택 변경 가능성
- 높은 테스트 커버리지 요구
- 여러 UI (Web, Mobile, API)
- 레거시 시스템 마이그레이션

❌ 부적합한 경우:
- 단순 CRUD
- 빠른 MVP
- 소규모 팀 (3명 미만)
- 단기 프로젝트
- 개인 프로젝트
```

---
<a id="Pattern-Comparison-and-Selection-Guide"></a>
## 7️⃣ 패턴 비교와 선택 가이드

### 📊 5가지 패턴 종합 비교

| 항목 | 계층형 | 도메인 중심 | 혼합 | DDD | 헥사고날/클린 |
|:---:|:---:|:---:|:---:|:---:|:---:|
| **학습 난이도** | ⭐ | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **초기 구축 시간** | 빠름 ⚡ | 보통 | 보통 | 느림 | 매우 느림 |
| **확장성** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **유지보수성** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **테스트 용이성** | ⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **팀 협업** | ⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **적용 난이도** | 쉬움 | 보통 | 어려움 | 어려움 | 매우 어려움 |
| **코드량** | 적음 | 보통 | 많음 | 많음 | 매우 많음 |

---

### 🎯 프로젝트 규모별 추천

#### 소규모 (도메인 1-5개, 팀 1-2명)

```
1순위: 계층형 구조
2순위: 도메인 중심 구조

이유:
- 빠른 개발 속도
- 낮은 학습 곡선
- 충분한 유지보수성
```

---

#### 중규모 (도메인 5-15개, 팀 3-5명)

```
1순위: 도메인 중심 구조 ⭐
2순위: 계층 + 도메인 혼합

이유:
- 기능 단위 개발 용이
- 팀원별 도메인 할당 가능
- 확장성 확보
```

---

#### 대규모 (도메인 15개 이상, 팀 6명 이상)

```
1순위: DDD 스타일
2순위: 헥사고날/클린 아키텍처

이유:
- 복잡한 비즈니스 로직 관리
- 명확한 책임 분리
- 장기 유지보수
```

---

### 🚀 프로젝트 유형별 추천

#### 💼 비즈니스 플랫폼 (커머스, 핀테크)

```
추천: DDD 스타일
이유: 복잡한 비즈니스 규칙, 도메인 전문가 협업
```

#### 📱 모바일 백엔드 API

```
추천: 도메인 중심 구조
이유: 명확한 기능 단위, RESTful API
```

#### 🔧 관리자 시스템 (CMS, Admin)

```
추천: 계층형 구조
이유: 단순 CRUD 중심, 빠른 개발
```

#### 🏢 레거시 마이그레이션

```
추천: 헥사고날/클린 아키텍처
이유: 점진적 전환, 기술 독립성
```

#### 🎓 학습용 프로젝트

```
추천: 계층형 → 도메인 중심
이유: 단계적 학습, 실무 패턴 익히기
```

---

### 📈 성장 단계별 로드맵

```
[1단계] 계층형 구조 (1-3개월)
   ↓
기본 Spring Boot 익히기
Controller, Service, Repository 이해
   ↓
[2단계] 도메인 중심 구조 (3-6개월)
   ↓
기능 단위 패키지 구성
도메인 경계 설정 연습
   ↓
[3단계] DDD 도입 (6-12개월)
   ↓
Value Object, Aggregate 이해
도메인 이벤트 활용
   ↓
[4단계] 헥사고날/클린 아키텍처 (1년 이상)
   ↓
Port & Adapter 패턴
의존성 역전 원칙 실전 적용
```

---
<a id="Essential-Shared-Package-Structure"></a>
## 8️⃣ 필수 공통 패키지 구성

### 📁 global/config (설정)

```
global/config/
├─ SecurityConfig.java         # Spring Security 설정
├─ WebConfig.java              # CORS, Interceptor
├─ JpaConfig.java              # JPA Auditing 등
├─ SwaggerConfig.java          # API 문서화
├─ AsyncConfig.java            # 비동기 처리
└─ RedisConfig.java            # 캐시 설정
```

#### 예시: SecurityConfig

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );
        
        return http.build();
    }
}
```

---

### 📁 global/error (예외 처리)

```
global/error/
├─ GlobalExceptionHandler.java # @RestControllerAdvice
├─ ErrorCode.java              # Enum
├─ ErrorResponse.java          # 에러 응답 DTO
└─ 📁 exception/
    ├─ BusinessException.java
    ├─ NotFoundException.java
    └─ UnauthorizedException.java
```

#### 예시: GlobalExceptionHandler

```java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException e) {
        log.error("NotFoundException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(ErrorCode.NOT_FOUND, e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }
    
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.error("BusinessException: {}", e.getMessage());
        ErrorResponse response = ErrorResponse.of(e.getErrorCode());
        return ResponseEntity.status(e.getHttpStatus()).body(response);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
```

#### 예시: ErrorCode

```java
@Getter
@AllArgsConstructor
public enum ErrorCode {
    
    // Common
    INVALID_INPUT_VALUE(400, "C001", "잘못된 입력값입니다."),
    NOT_FOUND(404, "C002", "리소스를 찾을 수 없습니다."),
    INTERNAL_SERVER_ERROR(500, "C003", "서버 오류가 발생했습니다."),
    
    // Post
    POST_NOT_FOUND(404, "P001", "게시글을 찾을 수 없습니다."),
    POST_ALREADY_PUBLISHED(400, "P002", "이미 발행된 게시글입니다."),
    
    // User
    USER_NOT_FOUND(404, "U001", "사용자를 찾을 수 없습니다."),
    DUPLICATE_EMAIL(400, "U002", "이미 사용중인 이메일입니다.");
    
    private final int status;
    private final String code;
    private final String message;
}
```

---

### 📁 global/common (공통 클래스)

```
global/common/
├─ BaseTimeEntity.java         # 생성/수정 시간
├─ ApiResponse.java            # 공통 응답 형식
├─ PageResponse.java           # 페이징 응답
└─ 📁 constant/
    ├─ ResponseMessage.java
    └─ ApiPath.java
```

#### 예시: BaseTimeEntity

```java
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Getter
public abstract class BaseTimeEntity {
    
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
```

#### 예시: ApiResponse

```java
@Getter
@AllArgsConstructor
public class ApiResponse<T> {
    
    private boolean success;
    private T data;
    private String message;
    
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, "성공");
    }
    
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, data, message);
    }
    
    public static <T> ApiResponse<T> fail(String message) {
        return new ApiResponse<>(false, null, message);
    }
}
```

---

### 📁 global/util (유틸리티)

```
global/util/
├─ JwtUtil.java                # JWT 토큰 처리
├─ DateUtil.java               # 날짜 유틸
├─ StringUtil.java             # 문자열 처리
└─ ValidationUtil.java         # 검증 유틸
```

---

### 📁 global/security (보안)

```
global/security/
├─ JwtAuthenticationFilter.java
├─ JwtTokenProvider.java
├─ CustomUserDetailsService.java
└─ SecurityUtil.java
```

---
<a id="Migration-Strategy"></a>
## 9️⃣ 마이그레이션 전략

### 🔄 계층형 → 도메인 중심 전환

#### Step 1: 도메인 식별

```
현재 Controller/Service/Repository를
어떤 도메인으로 묶을지 결정

예시:
- PostController, PostService, PostRepository → post
- CommentController, CommentService → comment
- UserController, UserService → user
```

---

#### Step 2: 점진적 이동

```java
// Before (계층형)
controller/
  ├─ PostController
  └─ CommentController
service/
  ├─ PostService
  └─ CommentService

// After (도메인 중심) - 단계적 이동
post/
  ├─ PostController       // ← 먼저 이동
  ├─ PostService          // ← 다음 이동
  └─ PostRepository       // ← 마지막 이동
  
comment/
  └─ (아직 이동 안 함)
```

---

#### Step 3: Import 경로 수정

```java
// Before
import com.devkobe.blog.controller.PostController;
import com.devkobe.blog.service.PostService;

// After
import com.devkobe.blog.post.PostController;
import com.devkobe.blog.post.PostService;
```

---

#### Step 4: 테스트 검증

```java
@SpringBootTest
class PostServiceTest {
    
    @Autowired
    private PostService postService;
    
    @Test
    void 구조_변경_후_정상_동작_확인() {
        // 기존 테스트 모두 통과하는지 확인
    }
}
```

---

### 🎯 마이그레이션 체크리스트

- [ ] 도메인 경계 명확히 정의
- [ ] 한 번에 하나의 도메인만 이동
- [ ] 이동 후 즉시 테스트 실행
- [ ] Import 경로 일괄 수정 (IDE 리팩토링 기능 활용)
- [ ] Git 커밋을 도메인 단위로
- [ ] 팀원과 충돌 최소화 (작은 단위로)
- [ ] 문서 업데이트 (README, 위키)

---
<a id="Key-Takeaways"></a>
## 🔟 핵심 요약

### 💡 가장 중요한 메시지

> **"완벽한 구조부터 만들려 하지 말고, 확장 가능한 구조로 시작하자"**

```
❌ 잘못된 접근:
"처음부터 DDD/헥사고날로 완벽하게!"
→ 오버엔지니어링, 프로젝트 실패

✅ 올바른 접근:
"계층형 또는 도메인 중심으로 시작"
→ 필요에 따라 점진적 개선
```

---

### 📌 단계별 추천 로드맵

| 단계 | 추천 구조 | 적용 시점 |
|:---:|:---:|---|
| **시작** | 계층형 | Spring Boot 입문, 개인 프로젝트 |
| **성장** | 도메인 중심 | 도메인 5개 이상, 팀 협업 |
| **성숙** | DDD | 복잡한 비즈니스, 장기 운영 |
| **전문** | 헥사고날/클린 | 대규모 서비스, MSA |

---

### 🎓 실무 체크리스트

#### 프로젝트 시작 전
- [ ] 예상 도메인 개수 파악
- [ ] 팀 규모와 경험 수준 고려
- [ ] 프로젝트 운영 기간 예상 (단기/장기)
- [ ] 비즈니스 복잡도 평가

#### 구조 설계 시
- [ ] 도메인 경계 명확히 정의
- [ ] 네이밍 규칙 팀 합의
- [ ] 공통 패키지 구성 (global)
- [ ] 환경별 설정 파일 분리

#### 개발 진행 중
- [ ] 패키지 구조 일관성 유지
- [ ] 순환 참조 방지
- [ ] 계층 간 책임 명확히
- [ ] 정기적인 리팩토링

---

### 📊 Quick Reference (빠른 선택 가이드)

```
나의 상황:
├─ 첫 Spring Boot 프로젝트
│   → 계층형 구조 ⭐
│
├─ 도메인 5-10개, 팀 3-5명
│   → 도메인 중심 구조 ⭐⭐⭐
│
├─ 복잡한 비즈니스 로직, 장기 운영
│   → DDD 스타일 ⭐⭐⭐⭐
│
└─ 레거시 마이그레이션, 기술 독립성 중요
    → 헥사고날/클린 아키텍처 ⭐⭐⭐⭐⭐
```

---

## 🚀 다음 학습 주제

- MSA(Microservices Architecture) 구조 설계
- Event Sourcing & CQRS 패턴
- Multi-Module 프로젝트 구성
- 테스트 전략과 구조
- CI/CD 파이프라인 구성

---

## 💬 FAQ

<details>
<summary><strong>Q1. 계층형에서 도메인 중심으로 언제 전환해야 하나요?</strong></summary>

**전환 시점 신호:**
- Controller에 파일이 10개 이상 쌓임
- 기능 하나 수정 시 3-4개 패키지를 넘나듦
- 팀원이 "이 파일 어디 있어요?" 자주 묻기 시작
- 도메인이 5개 이상으로 늘어남

**전환 방법:**
1. 새로운 도메인부터 도메인 중심으로 작성
2. 기존 코드는 천천히 이동 (급하지 않음)
3. 한 번에 하나의 도메인만 이동
4. 테스트가 잘 갖춰져 있으면 더 안전
</details>

<details>
<summary><strong>Q2. DDD를 배우지 않고도 도메인 중심 구조를 사용할 수 있나요?</strong></summary>

**네, 가능합니다!**

도메인 중심 구조는 DDD의 일부 개념만 차용한 것입니다.

**필요한 것:**
- 도메인 경계 식별 (어떤 기능끼리 묶을지)
- 패키지로 도메인 분리
- 기본 계층 구조 이해

**필요 없는 것 (DDD 고급 개념):**
- Aggregate Root
- Value Object
- Domain Event
- Bounded Context

**권장:**
도메인 중심 구조로 시작 → 필요하면 DDD 개념 점진적 도입
</details>

<details>
<summary><strong>Q3. 모든 도메인에 Controller, Service, Repository가 다 필요한가요?</strong></summary>

**아니요, 필요한 것만 만드세요.**

```java
// 단순 조회만 하는 도메인
tag/
        ├─ TagController
  ├─ TagRepository      // Service 없이도 OK
  └─ Tag

// 복잡한 비즈니스 로직이 있는 도메인
payment/
        ├─ PaymentController
  ├─ PaymentService     // 필수!
  ├─ PaymentValidator
  ├─ PaymentRepository
  └─ Payment
```

**원칙:**
- CRUD만 있으면 Service 생략 가능
- 비즈니스 로직 있으면 Service 필수
- 읽기만 하면 Repository만으로도 충분
</details>

<details>
<summary><strong>Q4. global 패키지에는 무엇을 넣어야 하나요?</strong></summary>

**넣어야 할 것:**
- 모든 도메인이 공통으로 사용하는 것
- 설정 (Config)
- 예외 처리 (Exception, ErrorCode)
- 공통 응답 형식 (ApiResponse)
- 유틸리티 (JwtUtil, DateUtil)
- 보안 (Security)

**넣으면 안 되는 것:**
- 특정 도메인에만 쓰이는 것
- 비즈니스 로직
- 도메인 엔티티
- DTO

**예시:**
```
✅ global/error/ErrorCode.java  (모든 도메인 사용)
❌ global/PostValidator.java    (post 도메인에 넣기)
```
</details>

<details>
<summary><strong>Q5. 헥사고날 아키텍처는 언제 사용해야 하나요?</strong></summary>

**사용해야 할 때:**
- DB를 MySQL → PostgreSQL로 바꿀 계획
- Spring Boot → Quarkus 같은 프레임워크 전환 가능성
- 테스트 커버리지 90% 이상 목표
- 레거시 시스템을 점진적으로 교체
- 5년 이상 장기 운영

**사용하지 말아야 할 때:**
- 단순 CRUD 서비스
- 6개월 미만 단기 프로젝트
- MVP 빠르게 만들기
- 팀원 대부분이 초급
- 개인 프로젝트

**대안:**
처음엔 도메인 중심으로 시작 → 필요시 헥사고날로 전환
</details>

---

## 📚 참고 자료

### 공식 문서
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Domain-Driven Design Reference](https://www.domainlanguage.com/ddd/reference/)

### 추천 도서
- "도메인 주도 설계" - 에릭 에반스
- "클린 아키텍처" - 로버트 C. 마틴
- "만들면서 배우는 클린 아키텍처" - 톰 홈버그

### 추천 강의
- 인프런: "실전! 스프링 부트와 JPA 활용" (김영한)
- 인프런: "스프링 핵심 원리 - 고급편" (김영한)

---

**🎉 이제 프로젝트 구조를 자신있게 설계할 수 있습니다!**

> 💡 **마지막 조언**: 완벽한 구조는 없습니다. 팀과 프로젝트에 맞는 구조를 선택하고, 필요에 따라 점진적으로 개선하세요. 가장 중요한 것은 **일관성**입니다!
