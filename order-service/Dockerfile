FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/order-service-*.jar app.jar
EXPOSE 8082
CMD ["java", "-jar", "app.jar"]