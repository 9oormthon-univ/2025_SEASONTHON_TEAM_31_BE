# 런타임 전용 (이미 Actions에서 ./gradlew bootJar 로 빌드 완료)
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# bootJar 산출물 경로 (필요시 파일명 조정)
ARG JAR_FILE=build/libs/*SNAPSHOT.jar
COPY ${JAR_FILE} app.jar

EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]