#
# Build stage
#
FROM maven:3.6.0-jdk-11-slim AS build
WORKDIR /workspace/app
COPY pom.xml .
COPY src src
RUN mvn -f pom.xml install -DskipTests
RUN ls -lsa target

#
# Package stage
#
FROM adoptopenjdk/openjdk11:alpine-jre
COPY --from=build /workspace/app/target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["sh", "-c", "java -jar /app.jar"]