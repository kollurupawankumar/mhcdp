MDHP Ingestion Service

Overview
- Ingestion stage; triggers PySpark ingestion job via SparkRunner.
- Uses outbox + log entries for reliability and visibility.

How to run locally
- Start services via docker-compose (root): docker-compose up --build
- Endpoints: interacts via the pipeline API gateway; ingestion listens to mdhp.pipeline.ingestion.request

Configuration
- Spark runner type: -Dspark.runner.type=LOCAL or EMR
- IO defaults via system properties; per-run overrides supported via payload

- Testing: curl or UI to trigger ingestion flow
