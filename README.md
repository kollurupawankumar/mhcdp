- MDHP: Metadata-Driven Healthcare Platform

Overview
- MDHP is a local-first, metadata-driven platform for ingesting, transforming, and enriching healthcare data using PySpark compute, Kafka orchestration, and a medallion architecture stored in a dockerized database.
- All dataset and pipeline configurations live in a DB and are UI-managed (no YAML).
- Compute is PySpark-based; local development uses a Docker Spark stack; production uses EMR.
- Data is stored using a Medallion architecture (raw → silver → gold) inside a dockerized DB (Postgres by default).
- Kafka orchestrates stage transitions and event-driven state progression.
- Observability and reliability are key: retries, DLQ, outbox, reconciler, and idempotent consumers.
- MDHP is a local-first, metadata-driven platform for ingesting, transforming, and enriching healthcare data using PySpark compute, Kafka orchestration, and a medallion architecture stored in a dockerized database.
- All dataset definitions, pipelines, and transformation rules are stored as metadata in a DB and managed via a React UI.
- The platform runs locally via Docker Compose for development; EMR is planned for production.

Tech Stack (Summary)
- UI: React + Vite (mhcp-platform-ui)
- Backend: Spring Boot 3 (Java 17) microservices
- Compute: PySpark; Local Spark stack in Docker; EMR in prod
- Messaging: Kafka + Zookeeper
- DB: Postgres (default), CockroachDB option
- Observability: OpenTelemetry, Prometheus, Grafana, Loki, Tempo

- Microservices & Modules (high-level)
- mhcp-platform-ui: UI for metadata editing and run control
- mhcp-api-gateway: API gateway with OpenAPI docs
- mhcp-metadata-service: CRUD for metadata and migrations (source_metadata, transformation_metadata, enrichment_metadata)
- mhcp-orchestrator: Schedules runs and emits ingestion/transformation/enrichment requests
- mhcp-ingestion-service: Ingestion stage; triggers PySpark ingestion job
- mhcp-transformation-service: Transformation stage; triggers PySpark transformation job
- mhcp-enrichment-service: Enrichment stage; triggers PySpark enrichment job / SQL execution
- mhcp-spark-runner: Spark runner abstraction (LOCAL/EMR)
- mhcp-reconciler: Runtime reconciler scaffold
- doc: ADRs, PRD, diagrams, runbook
- prometheus, grafana, loki, tempo: Observability stack (via docker-compose)

Getting Started (Local)
- Prereqs: Docker, Docker Compose, and Java (Mongo not required)
- Steps:
  1) mvn -DskipTests install
  2) docker-compose up --build
  3) UI: http://localhost:5173
  4) API docs: http://localhost:8080/swagger-ui.html
  5) Trigger a run: curl -X POST -H 'Content-Type: application/json' -d '{"subjectArea":"Claims","entityName":"Claim","entityVersion":"v1"}' http://localhost:8080/pipeline_runs

Where to look
- ADRs: doc/ADR-*.md (001-007)
- Runbook: README-Runbook.md
- Architectures & diagrams: doc/diagrams/*.svg (and Mermaid in Markdown)
- Documentation for onboarding new datasets is in doc/PRD_MDHP_Phase6plus.md

Contributing
- Follow ADRs for architectural decisions; add new ADRs as the design evolves
- Write tests for new components and document interfaces
- Keep docs updated with system changes

Contact
- If you want branch-based PRs or a different PR template, tell me and I’ll adapt.
- Prereqs: Docker, Docker Compose, and Java (Mongo not required)
- Steps:
  1) mvn -DskipTests install
  2) docker-compose up --build
  3) UI: http://localhost:5173
  4) API docs: http://localhost:8080/swagger-ui.html
- Onboarding datasets: metadata changes via UI and pipeline run triggers via API

Architecture and Services
- UI: mhcp-platform-ui (React + Vite)
- API Gateway: mhcp-api-gateway (Spring Cloud Gateway with OpenAPI docs)
- Metadata Service: mhcp-metadata-service (Spring Boot, Flyway migrations)
- Orchestrator: mhcp-orchestrator (Spring Boot)
- Stage services:
  - mhcp-ingestion-service (Ingestion)
  - mhcp-transformation-service (Transformation)
  - mhcp-enrichment-service (Enrichment)
- Spark Runner: mhcp-spark-runner (LOCAL by default; EMR placeholder)
- Reconciler: mhcp-reconciler (Outbox reconciler scaffold)
- DB: Postgres default; CockroachDB optional
- Kafka: mdhp.pipeline.* topics
- Observability: OpenTelemetry + Prometheus/Grafana/Loki/Tempo

How to customize local runs
- Spark runner fail-fast toggle: -Dspark.runner.failFast=false to test non-fatal paths
- EMR mode: -Demr.enabled=true to enable EMR path (requires EMR setup)
- IO overrides: payload can include inputPath/outputPath/scriptPath; or use system properties for defaults per stage
