FROM openjdk:11-jdk-slim
FROM maven:3.8.2-jdk-11
COPY . .
RUN mvn clean install -Pprod -DskipTests
ADD srfgroup-0.0.1-SNAPSHOT.jar springboot-docker-compose.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","springboot-docker-compose.jar"]
