# 1. 빌드(Build) 스테이지: Maven을 사용하여 프로젝트를 빌드합니다.
#FROM maven:3.8.5-openjdk-17 AS build
FROM maven:3-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# 2. 실행(Run) 스테이지: 빌드된 JAR 파일만 복사하여 최종 이미지를 만듭니다.
#FROM openjdk:17-jdk-slim
FROM openjdk:21-slim
WORKDIR /app
COPY --from=build /app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]