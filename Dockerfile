# Etapa 1: construir la app con Maven y Java 21
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# Etapa 2: imagen final minimalista con solo el JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
