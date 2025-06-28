FROM eclipse-temurin:21
ARG JAR_FILE=./target/wannab-book-service.jar
COPY ${JAR_FILE} wannab-book-service.jar

ENTRYPOINT ["java","-jar", "/wannab-book-service.jar"]