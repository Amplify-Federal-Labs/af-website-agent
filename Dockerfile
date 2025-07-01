# Multi-stage build Dockerfile

# Builder stage
FROM cgr.dev/chainguard/maven:latest AS builder

# Set working directory
WORKDIR /app

# Copy the project files
COPY pom.xml .
COPY src ./src
COPY .git .git

# Build the application
RUN mvn clean package -DskipTests

# Runtime stage
FROM cgr.dev/chainguard/jre

# Set working directory
WORKDIR /app

# Copy the built JAR from the builder stage
COPY --from=builder /app/target/spring-ai-agent-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]