ADR 003: Observability & Telemetry

Status: Proposed

Context
- Observability requires metrics, logs, and traces across UI, gateway, orchestration, and stage services.
- UI sends correlation identifiers (runId, entityName, stage) through the call chain; we need standardized propagation.
- The environment includes Prometheus, Grafana, Loki, and Tempo, with OpenTelemetry planning.

Decision
- Instrument all services with OpenTelemetry SDKs and propagate context across calls via the W3C Trace Context headers.
- Use Prometheus client libraries for metrics: stage duration histograms, success/failure counters, DLQ counts, and Spark job timings.
- Export traces to Tempo (and optionally Jaeger) for centralized analysis.
- Include structured logs with fields runId, entityName, stage, and jobIdentifier to ease log correlation.
- UI should surface a minimal set of observability indicators (e.g., last run, stage statuses) and link to dashboards as available.

Consequences
- Improves diagnosability and troubleshooting across the pipeline.
- Increases instrumentation surface area; requires consistent header propagation and context management.

Rationale
- Given the critical nature of healthcare data pipelines, end-to-end observability is essential for reliability and SLA adherence.

Status: Proposed
