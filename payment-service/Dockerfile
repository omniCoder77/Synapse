FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/payment-service-*.jar app.jar
EXPOSE 8083
CMD ["java", "-jar", "app.jar"]