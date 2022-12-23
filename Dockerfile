FROM openjdk:17-alpine

ARG JAR_FILE=target/BsBoard-*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]