# -------------------------------------------------------------
# Base stage: JDK 21 and Maven dependencies caching
# -------------------------------------------------------------
FROM eclipse-temurin:21-jdk-alpine AS base
WORKDIR /app

# Install curl for healthchecks
RUN apk add --no-cache curl

# Copy Maven wrapper and POM first for layer caching
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./
RUN chmod +x ./mvnw

# Pre-fetch project dependencies
RUN ./mvnw dependency:go-offline -B

# -------------------------------------------------------------
# Development stage: runs via spring-boot:run with DevTools
# -------------------------------------------------------------
FROM base AS dev
ENV SPRING_PROFILES_ACTIVE=dev
EXPOSE 8080
CMD ["./mvnw", "spring-boot:run"]

# -------------------------------------------------------------
# Builder stage: compile and package the jar
# -------------------------------------------------------------
FROM base AS builder
COPY src ./src
RUN ./mvnw clean package -DskipTests -B

# -------------------------------------------------------------
# Runtime stage: minimal JRE image
# -------------------------------------------------------------
FROM eclipse-temurin:21-jre-alpine AS runtime
WORKDIR /app

# Install curl for container healthcheck
RUN apk add --no-cache curl

# Run as non-root user for security
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser:appgroup

# Copy built artifact
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
