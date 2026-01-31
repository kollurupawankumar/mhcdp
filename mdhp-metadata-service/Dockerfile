# Build stage
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /workspace

# Copy everything (parent pom + modules)
COPY . .

# Build only required module (faster)
RUN mvn clean package -DskipTests -pl mdhp-metadata-service -am

# Runtime stage
FROM eclipse-temurin:17-jre
WORKDIR /app

COPY --from=build /workspace/mdhp-metadata-service/target/mdhp-metadata-service-0.0.1-SNAPSHOT.jar metadata-app.jar

EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/metadata-app.jar"]
