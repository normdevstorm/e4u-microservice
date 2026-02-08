# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY statisticservice ./statisticservice

RUN chmod +x ./gradlew && ./gradlew :statisticservice:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/statisticservice/build/libs/*.jar app.jar

EXPOSE 9082

ENTRYPOINT ["java", "-jar", "app.jar"]
