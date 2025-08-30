# 1) Build stage
FROM gradle:8.8-jdk17 AS build
WORKDIR /app

# 의존성 캐시용 최소 파일만 먼저 복사
COPY gradle gradle
COPY gradlew .
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 전체 소스 복사
COPY . .

# 🔧 여기서 다시 실행권한 부여 (앞 단계에서 덮여씀 방지)
RUN chmod +x gradlew && ./gradlew clean bootJar --no-daemon

# 2) Run stage
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENV TZ=Asia/Seoul
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar","--spring.profiles.active=${SPRING_PROFILE:dev}"]