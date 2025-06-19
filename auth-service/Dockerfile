FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/auth-service-*.jar app.jar
EXPOSE 8081
CMD ["java", "-jar", "app.jar"]