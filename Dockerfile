FROM maven:3.8.2-jdk-11
COPY . .
RUN mvn clean package -DskipTests

FROM openjdk:11-jdk-slim
ADD target/srfgroup-0.0.1-SNAPSHOT.jar springboot-docker-compose.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","springboot-docker-compose.jar"]
