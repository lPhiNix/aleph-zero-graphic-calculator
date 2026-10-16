# syntax=docker/dockerfile:1

# =============================================================================
# Aleph-Zero - single image: Spring Boot API + React SPA served from the same
# jar (same origin). The image contains no database: point ALEPHZERO_DB_URL at
# an existing MySQL instance or add your own service in docker-compose.
# =============================================================================

# ---------- Build stage ----------
FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /app

# The Node runtime downloaded by frontend-maven-plugin needs libatomic.
RUN apt-get update \
    && apt-get install -y --no-install-recommends libatomic1 \
    && rm -rf /var/lib/apt/lists/*

# Backend + frontend sources. The frontend is required by frontend-maven-plugin,
# which builds the React app and embeds it into the jar.
COPY pom.xml settings.xml ./
COPY src ./src
COPY frontend ./frontend

# Build the self-contained fat jar (dependencies + code + SPA).
RUN mvn -s settings.xml -B clean package -DskipTests

# ---------- Runtime stage ----------
FROM eclipse-temurin:21-jre

WORKDIR /app

# ---- Application configuration -------------------------------------------------
# Every setting the application reads. All of them can be overridden at runtime
# (docker run -e ... / docker compose environment / .env).
ENV ALEPHZERO_PORT=8080 \
    ALEPHZERO_DB_URL=jdbc:mysql://db:3306/mysqldb \
    ALEPHZERO_DB_USERNAME=root \
    ALEPHZERO_DB_PASSWORD=root \
    ALEPHZERO_JWT_SECRET=change-me-this-secret-must-be-at-least-32-bytes-long!! \
    ALEPHZERO_JWT_ACCESS_TTL=15m \
    ALEPHZERO_JWT_REFRESH_TTL=7d \
    ALEPHZERO_JWT_COOKIE_NAME=refresh_token \
    ALEPHZERO_JWT_COOKIE_SECURE=false \
    ALEPHZERO_FRONTEND_URL=http://localhost \
    ALEPHZERO_SWAGGER_ENABLED=false

EXPOSE 8080

COPY --from=build /app/target/*.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
