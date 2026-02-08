# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

# Copy all modules
COPY discoveryservice ./discoveryservice

# Build the specific service
RUN chmod +x ./gradlew && ./gradlew :discoveryservice:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/discoveryservice/build/libs/*.jar app.jar

EXPOSE 9999

ENTRYPOINT ["java", "-jar", "app.jar"]
