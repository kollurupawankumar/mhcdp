MDHP - Metadata Service

Overview
- Manages dataset/pipeline metadata for Source, Transformation, and Enrichment.
- Tables: source_metadata, transformation_metadata, enrichment_metadata.
- DB migrations are handled via Flyway; DB is Postgres by default.

What you can do here
- Create, read, update, delete metadata entries for: source, transformation, enrichment.
- Validate and activate versions via UI (activation workflow is implemented in the UI layer).

How to run locally
- Start the DB and services via docker-compose (root project):
  docker-compose up --build -d
-Migrate DB: Flyway will run on startup of the metadata service.
- Endpoints (examples):
  - GET http://localhost:8080/source-metadata
  - GET http://localhost:8080/transformation-metadata
  - GET http://localhost:8080/enrichment-metadata
- UI integration: UI (mhcp-platform-ui) calls these endpoints through the API gateway.

Notes
- This service is a core source of truth for metadata; changes here drive downstream orchestration.
- Ports and configuration are set by docker-compose. Adjust via environment if needed.
