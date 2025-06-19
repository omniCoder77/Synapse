FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/product-service-*.jar app.jar
EXPOSE 8084
CMD ["java", "-jar", "app.jar"]