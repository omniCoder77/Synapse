FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/search-service-*.jar app.jar
EXPOSE 8085
CMD ["java", "-jar", "app.jar"]