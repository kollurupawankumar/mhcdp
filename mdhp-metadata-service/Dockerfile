# Build stage
FROM maven:3.9.0-jdk-17 AS build
WORKDIR /workspace
COPY mdhp-metadata-service/pom.xml mdhp-metadata-service/pom.xml
COPY mdhp-metadata-service/src mdhp-metadata-service/src
RUN mvn -q -f mdhp-metadata-service/pom.xml clean package -DskipTests

# Runtime stage
FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=build /workspace/mdhp-metadata-service/target/mdhp-metadata-service-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app/app.jar"]
