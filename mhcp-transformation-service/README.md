MDHP Transformation Service

Overview
- Transformation stage; triggers PySpark transformation job via SparkRunner.
- Outbox + logs for reliability; supports per-run IO overrides.

How to run locally
- docker-compose up --build
- Endpoints: via gateway; transformation listens to mdhp.pipeline.transformation.request

Configuration
- Spark runner type: -Dspark.runner.type=LOCAL or EMR
- IO overrides via payload or system properties
