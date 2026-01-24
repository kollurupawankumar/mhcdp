MDHP Runbook (Phase 6+)

- What’s in place:
  - Metadata DB with source_metadata, transformation_metadata, enrichment_metadata
  - API Gateway, Orchestrator, and stage services: ingestion, transformation, enrichment
  - Spark runner with fail-fast toggle and configurable input/output/script paths
  - UI mhcp-platform-ui with editing capabilities for metadata and run trigger through gateway
  - Outbox and Reconciler scaffolds for reliability and eventual consistency

- How to run locally:
  1) mvn -DskipTests install
  2) docker-compose up --build
  3) Access UI at http://localhost:5173
  4) Trigger runs via API gateway: POST http://localhost:8080/pipeline_runs
  5) Edit metadata in UI and observe PUT updates propagating to backend DBs.

- How to tweak Spark Runner:
  - Set -Dspark.runner.failFast=false to disable fail-fast (for testing non-fatal paths)
  - Configure spark.script.* and spark.input.path / spark.output.path properties for each stage via system properties or payload overrides.

- ADRs are embedded in docs/adr directory for traceability.
- How to run locally:
  1) mvn -q -DskipTests install
  2) docker-compose up --build
  3) Access UI at http://localhost:5173
  4) Trigger runs via API gateway: POST http://localhost:8080/pipeline_runs
  5) Edit metadata in UI and observe PUT updates propagating to backend DBs.

- OpenAPI/Docs:
  - API docs served at: http://localhost:8080/swagger-ui.html (via SpringDoc OpenAPI UI)
  - You can also hit /v3/api-docs for the JSON spec.
