FROM maven:3.9.0-jdk-17 AS build
WORKDIR /workspace
COPY pom.xml mhcp-orchestrator/pom.xml
COPY mhcp-orchestrator/src ./mhcp-orchestrator/src
RUN mvn -q -f mhcp-orchestrator/pom.xml clean package -DskipTests

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /workspace/mhcp-orchestrator/target/mhcp-orchestrator-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
