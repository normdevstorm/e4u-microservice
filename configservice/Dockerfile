# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Copy all modules (needed for multi-module build)
COPY configservice ./configservice

# Build the specific service
RUN chmod +x ./gradlew && ./gradlew :configservice:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

# Add curl for healthcheck
RUN apk add --no-cache curl

# Copy the built jar
COPY --from=build /app/configservice/build/libs/*.jar app.jar

# Expose port
EXPOSE 8888

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
