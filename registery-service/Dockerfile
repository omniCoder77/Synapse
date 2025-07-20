FROM openjdk:21-jdk-slim as builder

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

RUN ./gradlew dependencies

COPY src ./src

RUN ./gradlew clean build -x test

FROM openjdk:21-jre-slim

WORKDIR /app

COPY keystore.jks .

COPY --from=builder /app/build/libs/*.jar app.jar

EXPOSE 8761

ENTRYPOINT ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]