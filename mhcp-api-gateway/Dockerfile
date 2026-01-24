FROM maven:3.9.0-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml mhcp-api-gateway/pom.xml
COPY mhcp-api-gateway/src ./mhcp-api-gateway/src
RUN mvn -q -f mhcp-api-gateway/pom.xml clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /workspace/mhcp-api-gateway/target/mhcp-api-gateway-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
