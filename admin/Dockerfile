# Use lightweight Java 21 runtime
FROM eclipse-temurin:21-jre

# Create app directory inside container
WORKDIR /app

# Copy the jar built by Maven
COPY target/*.jar app.jar

# Expose Spring Boot port
EXPOSE 8081

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
