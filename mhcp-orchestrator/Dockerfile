FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace
# Copy everything (parent pom + modules)
COPY . .
# Build only required module (faster)
RUN mvn -f pom.xml -pl mhcp-orchestrator -am clean package -DskipTests

FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/mhcp-orchestrator/target/mhcp-orchestrator-0.0.1-SNAPSHOT.jar orchestrator-app.jar
EXPOSE 8082
ENTRYPOINT ["java","-jar","/app/orchestrator-app.jar"]
