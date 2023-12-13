FROM openjdk:17
ARG JAR_FILE=target/*.jar
COPY ./target/lms-0.0.1-SNAPSHOT.jar lms-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/lms-0.0.1-SNAPSHOT.jar"]