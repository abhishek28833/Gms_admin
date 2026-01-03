# =========================
# Stage 1: Build (Java 21)
# =========================
FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /build

# Copy pom.xml first for dependency caching
COPY pom.xml .
RUN mvn dependency:go-offline

# Copy source code
COPY src ./src

# Build the jar
RUN mvn clean package -DskipTests


# =========================
# Stage 2: Runtime (Java 21)
# =========================
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

# Copy jar from builder stage
COPY --from=builder /build/target/*.jar app.jar

EXPOSE 8081

# Container-friendly JVM options
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
