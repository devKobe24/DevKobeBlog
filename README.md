# 📝 DevKobeBlog

> Git 기반 자동 동기화 블로그 시스템

**DevKobeBlog**는 GitHub 저장소의 마크다운 파일을 자동으로 동기화하여 웹 블로그로 변환하는 Git-Driven CMS입니다.
코드를 작성하듯 마크다운으로 글을 쓰고, Git Push만 하면 자동으로 블로그에 발행됩니다.

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

---

## 📋 목차

- [주요 기능](#-주요-기능)
- [기술 스택](#-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [시작하기](#-시작하기)
- [환경 설정](#-환경-설정)
- [GitHub Webhook 설정](#-github-webhook-설정)
- [마크다운 작성 가이드](#-마크다운-작성-가이드)
- [배포](#-배포)
- [API 명세](#-api-명세)
- [테스트](#-테스트)
- [트러블슈팅](#-트러블슈팅)
- [라이센스](#-라이센스)

---

## ✨ 주요 기능

### 🔄 자동 동기화
- **GitHub Webhook 연동**: Push 이벤트 발생 시 자동으로 블로그 동기화
- **Git Pull 자동화**: JGit을 활용한 저장소 자동 동기화
- **비동기 처리**: 동기화 작업이 블로그 성능에 영향을 주지 않음

### 📝 마크다운 기반 CMS
- **Front Matter 지원**: YAML 형식으로 제목, 날짜, 태그, 카테고리 등 메타데이터 관리
- **Flexmark 파서**: GitHub Flavored Markdown(GFM) 완벽 지원
- **코드 하이라이팅**: Prism.js를 통한 문법 강조

### 🖼️ 이미지 자동 관리
- **S3 자동 업로드**: 로컬 이미지를 자동으로 AWS S3에 업로드
- **경로 자동 변환**: 마크다운 내 이미지 경로를 S3 URL로 자동 치환
- **썸네일 처리**: Front Matter의 썸네일도 자동 S3 업로드

### 📂 유연한 분류 시스템
- **카테고리**: 폴더 구조 기반 자동 카테고리 생성
- **태그**: Front Matter에서 여러 태그 지정 가능
- **필터링**: 카테고리, 태그별 게시글 필터링 및 페이징

### 🔒 게시글 상태 관리
- **공개/비공개**: Front Matter의 `published` 필드로 제어
- **Soft Delete**: 파일 삭제 시 DB에서 논리 삭제 (복구 가능)
- **버전 관리**: Git 커밋 히스토리로 모든 변경사항 추적

---

## 🛠 기술 스택

### Backend
- **Framework**: Spring Boot 3.2.2
- **Language**: Java 17 (Amazon Corretto)
- **Build Tool**: Gradle 8.14.3
- **Template Engine**: Thymeleaf

### Database
- **Production**: MySQL 8.0
- **Development**: H2 Database
- **ORM**: Spring Data JPA / Hibernate

### Infrastructure
- **Container**: Docker & Docker Compose
- **Web Server**: Nginx 1.25
- **SSL**: Let's Encrypt (Certbot)
- **Cloud Storage**: AWS S3

### Libraries
- **Git Integration**: JGit 6.8.0
- **Markdown Parser**: Flexmark 0.64.8
- **AWS SDK**: AWS SDK for Java 2.x
- **Frontend**: TailwindCSS, Prism.js

---

## 📁 프로젝트 구조

```
DevKobeBlog/
│
├─ 📁 src/main/java/com/kobe/devkobeblog/
│   │
│   ├─ 📁 common/                    # 공통 모듈
│   │   ├─ 📁 component/
│   │   │   ├─ GitUtils.java         # Git 동기화 유틸
│   │   │   ├─ MarkdownProcessor.java # 마크다운 파싱 & 이미지 처리
│   │   │   └─ S3Uploader.java       # AWS S3 업로드
│   │   ├─ 📁 config/
│   │   │   ├─ AsyncConfig.java      # 비동기 처리 설정
│   │   │   └─ S3Config.java         # AWS S3 설정
│   │   └─ 📁 domain/
│   │       └─ BaseTimeEntity.java   # 생성/수정 시간 공통 엔티티
│   │
│   ├─ 📁 post/                      # 게시글 도메인
│   │   ├─ 📁 controller/
│   │   │   └─ BlogController.java   # 블로그 화면 컨트롤러
│   │   ├─ 📁 domain/
│   │   │   ├─ Post.java             # 게시글 엔티티
│   │   │   ├─ PostRepository.java
│   │   │   ├─ Category.java         # 카테고리 엔티티
│   │   │   ├─ CategoryRepository.java
│   │   │   ├─ Tag.java              # 태그 엔티티
│   │   │   ├─ TagRepository.java
│   │   │   └─ PostStatus.java       # PUBLIC, PRIVATE, DELETED
│   │   ├─ 📁 service/
│   │   │   └─ PostSyncService.java  # 동기화 비즈니스 로직
│   │   └─ 📁 dto/
│   │       └─ PostParseResult.java  # 파싱 결과 DTO
│   │
│   └─ 📁 sync/                      # 동기화 도메인
│       └─ 📁 controller/
│           └─ WebhookController.java # GitHub Webhook 엔드포인트
│
├─ 📁 src/main/resources/
│   ├─ 📁 templates/                 # Thymeleaf 템플릿
│   │   ├─ index.html                # 메인 페이지
│   │   ├─ post.html                 # 게시글 상세
│   │   └─ 📁 fragments/             # 공통 조각
│   └─ application.yml               # 설정 파일
│
├─ 📁 nginx/                         # Nginx 설정
│   └─ 📁 conf.d/
│       └─ app.conf                  # 프록시 & SSL 설정
│
├─ 📁 posts/                         # 블로그 글 저장소 (Git 동기화 대상)
│   ├─ 📁 Backend Development/
│   └─ 📁 [Category]/
│       └─ yyyy-mm-dd-title.md
│
├─ docker-compose.yml                # Docker Compose 설정
├─ Dockerfile                        # 멀티 스테이지 빌드
└─ build.gradle                      # Gradle 빌드 스크립트
```

---

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Docker & Docker Compose
- AWS 계정 (S3 사용)
- GitHub 계정

### 로컬 개발 환경 실행

#### 1. 저장소 클론

```bash
git clone https://github.com/devKobe24/DevKobeBlog.git
cd DevKobeBlog
```

#### 2. 환경 변수 설정

`.env` 파일을 프로젝트 루트에 생성:

```bash
# .env
DB_PASSWORD=your_mysql_password
AWS_ACCESS_KEY=your_aws_access_key
AWS_SECRET_KEY=your_aws_secret_key
WEBHOOK_SECRET=your_github_webhook_secret
```

#### 3. 애플리케이션 실행

**방법 1: Gradle 직접 실행**

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

**방법 2: Docker Compose 실행**

```bash
docker-compose up -d
```

#### 4. 브라우저 접속

```
http://localhost:8080
```

---

## ⚙️ 환경 설정

### application.yml 구조

```yaml
# 공통 설정
server:
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: true

spring:
  profiles:
    active: dev  # dev, prod

# AWS S3 설정
cloud:
  aws:
    region:
      static: ap-northeast-2
    s3:
      bucket: your-bucket-name
    credentials:
      access-key: ${AWS_ACCESS_KEY}
      secret-key: ${AWS_SECRET_KEY}

# Git 동기화 설정
blog:
  git:
    url: https://github.com/devKobe24/DevKobeBlog.git
    local-path: ./blog-data
    webhook-secret: ${WEBHOOK_SECRET}
```

### 프로파일별 설정

#### application-dev.yml (개발 환경)

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
```

#### application-prod.yml (운영 환경)

```yaml
spring:
  datasource:
    url: jdbc:mysql://blog-db:3306/blog_prod
    username: root
    password: ${DB_PASSWORD}
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: false
```

---

## 🔗 GitHub Webhook 설정

### 1. Webhook 생성

GitHub 저장소 → Settings → Webhooks → Add webhook

### 2. 설정 항목

| 항목 | 값 |
|---|---|
| **Payload URL** | `https://your-domain.com/api/webhook/sync` |
| **Content type** | `application/json` |
| **Secret** | `.env` 파일의 `WEBHOOK_SECRET` 값 |
| **Events** | `Just the push event` |

### 3. 동작 확인

1. GitHub에 마크다운 파일 Push
2. Webhook 전송 확인 (GitHub Webhooks 페이지)
3. 블로그에서 게시글 확인

---

## ✍️ 마크다운 작성 가이드

### Front Matter 필수 항목

```yaml
---
title: 게시글 제목
published: true  # 공개 여부 (true/false)
tags:
  - Java
  - Spring Boot
date: 2026-01-29
thumbnail: /assets/img/thumbnail.jpg
---

# 본문 시작

여기에 마크다운 내용을 작성합니다.
```

### 파일 구조 예시

```
posts/
├─ Backend Development/
│   ├─ 2026-01-29-spring-boot-project-structure.md
│   └─ 2026-01-28-jpa-basics.md
├─ Frontend/
│   └─ 2026-01-27-react-hooks.md
└─ assets/
    └─ img/
        ├─ thumbnail.jpg
        └─ diagram.png
```

### 이미지 경로 작성법

#### 절대 경로 (Git Root 기준)

```markdown
![이미지](/assets/img/diagram.png)
```

#### 상대 경로 (현재 파일 기준)

```markdown
![이미지](./img/screenshot.png)
```

> 💡 **자동 변환**: 로컬 이미지는 자동으로 S3에 업로드되고 URL이 치환됩니다.

### 지원 마크다운 문법

- ✅ GitHub Flavored Markdown (GFM)
- ✅ 표(Table)
- ✅ 체크박스(Task List)
- ✅ 코드 블록(Syntax Highlighting)
- ✅ 수식(Math) - 확장 가능

---

## 🐳 배포

### Docker Compose 구조

```yaml
services:
  nginx:        # 웹 서버 & 리버스 프록시
  certbot:      # SSL 인증서 자동 갱신
  blog-app:     # Spring Boot 애플리케이션
  blog-db:      # MySQL 데이터베이스
```

### 운영 환경 배포

#### 1. 환경 변수 설정

```bash
# .env 파일 생성 (서버에서)
nano .env
```

#### 2. SSL 인증서 발급

```bash
# Certbot으로 Let's Encrypt 인증서 발급
docker-compose run --rm certbot certonly --webroot \
  -w /var/www/certbot \
  -d your-domain.com \
  --email your-email@example.com \
  --agree-tos \
  --no-eff-email
```

#### 3. 애플리케이션 실행

```bash
docker-compose up -d --build
```

#### 4. 로그 확인

```bash
# 전체 로그
docker-compose logs -f

# 특정 서비스 로그
docker-compose logs -f blog-app
```

### 업데이트 배포

```bash
# 1. 최신 코드 Pull
git pull origin main

# 2. 재빌드 및 재시작
docker-compose up -d --build blog-app

# 3. 이전 이미지 정리
docker image prune -f
```

---

## 📡 API 명세

### Webhook 엔드포인트

#### POST `/api/webhook/sync`

GitHub에서 Push 이벤트 발생 시 호출되는 엔드포인트

**Headers**
```http
X-Hub-Signature-256: sha256={HMAC}
Content-Type: application/json
```

**Request Body**
```json
{
  "ref": "refs/heads/main",
  "commits": [...]
}
```

**Response**
```http
HTTP/1.1 200 OK
Content-Type: text/plain

Sync started
```

**Error Response**
```http
HTTP/1.1 401 Unauthorized
Content-Type: text/plain

Invalid Signature
```

---

## 🧪 테스트

### 테스트 실행

```bash
# 전체 테스트
./gradlew test

# 특정 테스트 클래스
./gradlew test --tests WebhookControllerTest

# 통합 테스트만
./gradlew test --tests '*IntegrationTest'
```

### 테스트 커버리지

```bash
./gradlew test jacocoTestReport
```

리포트 확인: `build/reports/jacoco/test/html/index.html`

### 주요 테스트

| 테스트 클래스 | 테스트 대상 | 타입 |
|---|---|:---:|
| `GitUtilsTest` | Git Clone/Pull 로직 | 단위 |
| `MarkdownProcessorTest` | 마크다운 파싱 & 이미지 업로드 | 단위 |
| `S3UploaderTest` | S3 업로드 | 단위 |
| `PostSyncServiceTest` | 동기화 비즈니스 로직 | 단위 |
| `WebhookControllerTest` | Webhook 서명 검증 | 단위 |
| `WebhookIntegrationTest` | End-to-End 동기화 흐름 | 통합 |

---

## 🔧 트러블슈팅

### 문제 1: Webhook이 동작하지 않음

**증상**: GitHub에 Push했지만 블로그가 업데이트되지 않음

**해결**:
1. GitHub Webhook 설정 확인
    - Payload URL이 정확한지 확인
    - Secret이 `.env`의 값과 일치하는지 확인
2. 서버 로그 확인
   ```bash
   docker-compose logs -f blog-app
   ```
3. Webhook 전송 내역 확인 (GitHub → Settings → Webhooks → Recent Deliveries)

### 문제 2: 이미지가 업로드되지 않음

**증상**: 마크다운 이미지가 깨져서 표시됨

**해결**:
1. AWS S3 권한 확인
    - IAM 사용자에게 S3 PutObject 권한이 있는지 확인
2. 이미지 경로 확인
    - 절대 경로: `/assets/img/image.png`
    - 상대 경로: `./img/image.png`
3. 로그에서 S3 업로드 오류 확인

### 문제 3: Docker 빌드 실패

**증상**: `docker-compose up` 시 빌드 에러

**해결**:
```bash
# 캐시 삭제 후 재빌드
docker-compose build --no-cache

# 이전 컨테이너 정리
docker-compose down -v
docker system prune -a
```

### 문제 4: MySQL 연결 실패

**증상**: `Could not create connection to database server`

**해결**:
1. MySQL 컨테이너 상태 확인
   ```bash
   docker-compose ps
   ```
2. 환경 변수 확인 (`.env` 파일의 `DB_PASSWORD`)
3. MySQL 로그 확인
   ```bash
   docker-compose logs blog-db
   ```

---

## 🤝 기여하기

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'feat: Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### 커밋 메시지 규칙

```
feat: 새로운 기능 추가
fix: 버그 수정
docs: 문서 수정
style: 코드 포맷팅, 세미콜론 누락 등
refactor: 코드 리팩토링
test: 테스트 코드
chore: 빌드 업무 수정, 패키지 매니저 수정
```

---

## 📄 라이센스

이 프로젝트는 MIT 라이센스를 따릅니다. 자세한 내용은 [LICENSE](LICENSE) 파일을 참고하세요.

---

## 👨‍💻 개발자

**devKobe24** - [@devKobe24](https://github.com/devKobe24)

프로젝트 링크: [https://github.com/devKobe24/DevKobeBlog](https://github.com/devKobe24/DevKobeBlog)

웹 페이지: [devKobe24 Blog](https://devkobe-blog.com/)

---

## 📚 참고 자료

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [JGit User Guide](https://wiki.eclipse.org/JGit/User_Guide)
- [Flexmark Java](https://github.com/vsch/flexmark-java)
- [AWS SDK for Java 2.x](https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/)
- [Docker Compose](https://docs.docker.com/compose/)

---

## 🔮 향후 계획

- [ ] 댓글 시스템 추가 (Utterances 연동)
- [ ] 검색 기능 구현
- [ ] RSS Feed 생성
- [ ] SEO 최적화 (메타 태그, 사이트맵)
- [ ] 다크 모드 지원
- [ ] 관리자 페이지 (게시글 관리, 통계)
- [ ] Elasticsearch 연동 (전문 검색)
- [ ] CDN 적용 (CloudFront)

---