# ./ (root) (backend dockerfile)
# Stage 1: maven and java
FROM maven:3.9.6-eclipse-temurin-21-alpine AS builder

WORKDIR /app
COPY pom.xml ./
COPY src ./src

# Download dependencies, using fist cache
RUN mvn dependency:go-offline

# Build package without tests
RUN mvn clean package -DskipTests

# Stage 2: final image with JAR
FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
