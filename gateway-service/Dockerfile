FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/gateway-service-*.jar app.jar
EXPOSE 8080
CMD ["java", "-jar", "app.jar"]