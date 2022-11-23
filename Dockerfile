#
# Build stage
#
FROM maven:3.8.2-jdk-11 AS build
COPY . .
RUN mvn clean package -DskipTests

#
# Package stage
#
FROM openjdk:11-jdk-slim
COPY --from=build /target/srfgroup-0.0.1-SNAPSHOT.jar falcon.jar
# ENV PORT=8080
EXPOSE 8080
ENTRYPOINT ["java","-jar","falcon.jar"]



#
# Build stage
#
#FROM maven:3.8.2-jdk-11
#COPY . .
#RUN mvn clean package -DskipTests
#
##
## Package stage
##
#FROM openjdk:11-jdk-slim
#COPY --from=build target/srfgroup-0.0.1-SNAPSHOT.jar springboot-docker-compose.jar
#ADD target/srfgroup-0.0.1-SNAPSHOT.jar springboot-docker-compose.jar
#EXPOSE 8080
#ENTRYPOINT ["java","-jar","springboot-docker-compose.jar"]
