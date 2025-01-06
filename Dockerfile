FROM openjdk:17-jdk-slim
ARG JAR_FILE=./build/libs/*.jar
COPY ./build/libs/WebNovel-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]