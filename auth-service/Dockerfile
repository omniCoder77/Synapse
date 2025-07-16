FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/auth-service-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
CMD ["java", "-XX:+UseContainerSupport", "-Djava.security.egd=file:/dev/./urandom", "-jar", "app.jar"]