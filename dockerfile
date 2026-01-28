# 1. Build Stage (Gradle 빌드 환경)
FROM amazoncorretto:17 AS builder
WORKDIR /app
COPY . .
# 테스트는 이미 통과했으므로 SKIP하고 빌드하여 속도 단축
RUN ./gradlew clean build -x test

# 2. Run Stage (실제 실행 환경 - 가벼운 이미지 사용)
FROM amazoncorretto:17
WORKDIR /app

# 빌드 스테이지에서 생성된 JAR 파일만 복사
COPY --from=builder /app/build/libs/*.jar app.jar

# 타임존 설정 (한국 시간)
ENV TZ=Asia/Seoul

# 실행 명령어
ENTRYPOINT ["java", "-jar", "app.jar"]
