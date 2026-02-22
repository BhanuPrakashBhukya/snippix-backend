FROM eclipse-temurin:21-jdk
WORKDIR /app

LABEL authors="Bhanu Prakash Bhukya"

COPY target/*.jar app.jar

EXPOSE 8082 5006

ENTRYPOINT ["java", "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5006", "-jar", "app.jar"]