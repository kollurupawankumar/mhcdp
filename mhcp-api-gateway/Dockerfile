FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy everything (parent pom + modules)
COPY . .
RUN apt-get update && apt-get install -y ca-certificates && update-ca-certificates
RUN mvn -f mhcp-api-gateway/pom.xml clean package -DskipTests

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/mhcp-api-gateway/target/mhcp-api-gateway-0.0.1-SNAPSHOT.jar api-app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/api-app.jar"]
