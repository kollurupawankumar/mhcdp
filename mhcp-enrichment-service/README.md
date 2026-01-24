MDHP Enrichment Service

Overview
- Enrichment stage; triggers PySpark enrichment or SQL-based paths via SparkRunner.
- Outbox + logs and a simple failure path with the fail-fast toggle.

How to run locally
- docker-compose up --build
- Enrichment listens on mdhp.pipeline.enrichment.request and emits mdhp.pipeline.enrichment.completed

Configuration
- Spark runner type: -Dspark.runner.type=LOCAL or EMR
- IO overrides via payload or system properties
