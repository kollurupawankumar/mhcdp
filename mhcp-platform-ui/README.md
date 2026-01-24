MDHP Platform UI

Overview
- React + Vite-based UI for managing metadata and running pipelines.
- UI talks to the API gateway to perform CRUD on metadata and to trigger runs.

How to run locally (UI only)
- Build and run via Docker Compose (preferred):
  docker-compose up --build
- Access UI at http://localhost:5173

UI structure
- Sections for Source Metadata, Transformation Metadata, Enrichment Metadata
- Edit fields like inputPath and outputPath (and scriptPath where applicable)
- Save actions persist to the API gateway

Run notes
- The UI depends on the API gateway; ensure mhcp-api-gateway and metadata service are up.
- For development, you can run the UI standalone with npm/yarn, but the Dockerized flow is recommended for parity.
