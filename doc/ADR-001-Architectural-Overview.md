ADR 001: Architectural Overview

Context
- MDHP is designed to be metadata-driven, event-driven, and local-first with PySpark compute, Kafka orchestration, and a medallion architecture stored in a dockerized database. Production uses EMR; local uses Docker Spark.
- UI manages metadata (no YAML); all metadata lives in a DB.
- Medallion: raw → silver → gold within a dockerized Postgres database.
- Kafka drives stage transitions (ingestion → transformation → enrichment).
- Reliability: retries, exponential backoff, DLQ, outbox, reconciler; consumers idempotent.

Decision
- Maintain a single, pluggable Spark runner abstraction to support LOCAL (development) and EMR (production).
- Implement UI editing for all three metadata domains; provide a clear path to activation/versioning.
- Use Flyway for DB migrations; ensure schema evolution via versioned migrations.
- Introduce an outbox and reconciler to ensure reliable event delivery and eventual consistency.
- Include Runbook, ADRs, and architecture diagrams for governance.

Consequences
- UI-driven metadata reduces YAML churn and onboarding time.
- EMR integration will be implemented in Phase 7 with AWS EMR SDK usage and config.
- Outbox + reconciler enables robust reliability and idempotence.

Rationale
- Metadata-driven pipelines improve onboarding speed and reduce maintenance costs.
- Event-driven, stage-based orchestration with Kafka enables loose coupling and fault tolerance.
- The medallion approach aligns with common data engineering best practices.

Quality attributes
- Correctness, Reliability, Observability, and Extensibility addressed via patterns (outbox, reconciler, idempotent consumers).

References
- ADR-004 Spark-Fail-Fast
- ADR-005 UI Editing UX
- ADR-006 EMR Runner Plan (Phase 7)
- ADR-007 Runbook Final
