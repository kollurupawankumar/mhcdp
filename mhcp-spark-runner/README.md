MDHP Spark Runner

Overview
- Spark runner abstraction for LOCAL and EMR. Local uses spark-submit; EMR is a placeholder for Phase 7 integration.

Default behavior
- Fail-fast toggle via spark.runner.failFast (default true)
- IO per-run: scriptPath, inputPath, outputPath; can be overridden via payload

- How to run
- Local: run via the host or container; specify properties as needed:
  - java -Dspark.runner.failFast=false -Dspark.script.ingestion=/path/to/ingest.py ...
- EMR: set spark.runner.type=EMR and provide emr.* config when implemented
-
- EMR details (Phase 7): Enable EMR path with -Demr.enabled=true and configure -Demr.region, -Demr.clusterId, -Demr.steps when ready.
