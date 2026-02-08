# Build stage
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle .
COPY settings.gradle .
COPY gradle.properties .

COPY paymentservice ./paymentservice

RUN chmod +x ./gradlew && ./gradlew :paymentservice:bootJar -x test --no-daemon

# Runtime stage
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

RUN apk add --no-cache curl

COPY --from=build /app/paymentservice/build/libs/*.jar app.jar

EXPOSE 9084

ENTRYPOINT ["java", "-jar", "app.jar"]
