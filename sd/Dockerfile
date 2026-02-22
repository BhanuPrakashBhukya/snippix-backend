FROM eclipse-temurin:21-jdk
WORKDIR /app

LABEL authors="Bhanu Prakash Bhukya"

COPY target/*.jar app.jar

EXPOSE 8761
ENTRYPOINT ["java", "-jar", "app.jar"]