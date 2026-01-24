MDHP Phase 6+ PRD

Overview
- Extend MDHP with a fail-fast, pluggable Spark runner (LOCAL by default) and an EMR-ready path, plus per-run IO configurability and UI-driven metadata editing. Maintain a reconciler scaffold for reliability and eventual consistency.

Goals
- Onboard datasets via metadata UI with per-run IO overrides and no YAML.
- Support multiple sources: FILE, DB, API, KAFKA; compute via PySpark; local-first with EMR in prod.
- Kafka-driven orchestration across ingestion → transformation → enrichment.
- Medallion storage (raw, silver, gold) in a dockerized Postgres/CockroachDB.
- Reliability: retries, exponential backoff, DLQ, and an outbox + reconciler.
- Local execution via Docker Compose; production via EMR.

Key Architecture Points
- UI (mhcp-platform-ui) → API Gateway (mhcp-api-gateway) → Metadata Service → Orchestrator → Stage Services → PySpark (Ingestion/Transformation/Enrichment) → Medallion DB.
- Spark runner abstraction with LOCAL + EMR placeholder; fail-fast toggle via spark.runner.failFast.
- IO overrides: scriptPath, inputPath, outputPath (payload-based and system-properties-based).
- Outbox pattern and per-stage logs for reliability.
- ADRs documenting decisions and a Runbook for operation.

Runtime & Environment
- Local: Docker Compose with Kafka, UI, gateway, metadata DB, stage services, and Spark (local).
- Production: EMR for Spark compute; MSK for Kafka; enterprise DB for metadata; OTel + Grafana for observability.

Tech Stack (Summary)
- UI: React + Vite
- Backend: Java 17, Spring Boot 3, Spring Kafka, Flyway
- Compute: PySpark (LOCAL via Docker Spark; EMR in prod)
- Messaging: Kafka
- DB: Postgres (default); CockroachDB (optional)
- Observability: OpenTelemetry, Prometheus, Grafana, Loki, Tempo

Success Metrics (Phase 6+)
- Onboard new entity < 30 minutes
- 100% pipeline run visibility in UI
- ≥ 98% stable pipeline success
- <15 minutes MTTD
- 100% DLQ and retry support
- 100% terminal publish of Spark jobs

Risks & Mitigations
- EMR readiness: plan Phase 7 EMR integration, keep LOCAL as default.
- Metadata misconfiguration: strong UI validation and version activation.
- Data volume: pluggable, docker-native DB, later lakehouse.

Next Steps
- Implement EMR runner end-to-end (Phase 7) and wire EMR configuration.
- Complete UI depth (validation, run/history, DLQ view).
- Add OpenAPI validation and a small test suite.

This document is intended for reviewers and operators to understand Phase 6+ scope and future evolution.
