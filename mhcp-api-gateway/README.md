MDHP API Gateway

Overview
- Spring Cloud Gateway providing routes to metadata and orchestrator services. Exposes OpenAPI UI at /swagger-ui.html.

How to run locally
- Build and run via Maven or Docker:
  - Maven: mvn -DskipTests package && java -jar mhcp-api-gateway/target/mhcp-api-gateway-*.jar
- Docker: docker-compose up mhcp-api-gateway

Endpoints (high level)
- Metadata routes proxied to metadata-service via gateway
- /swagger-ui.html for API docs

Notes
- OpenAPI docs are available via the gateway.
- Ensure the metadata service and kafka services are up for end-to-end tests.
