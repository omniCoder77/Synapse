FROM eclipse-temurin:21-jre-alpine-3.21
WORKDIR /app
COPY build/libs/config-service-*.jar app.jar
EXPOSE 8888
CMD ["java", "-jar", "app.jar"]