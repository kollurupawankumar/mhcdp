MDHP Orchestrator

Overview
- Schedules pipeline runs and emits ingestion.request events to drive the stage flow.
- Creates pipeline_run records and propagates run IDs downstream.

How to run locally
- docker-compose up --build
- API gateway routes to orchestrator; POST /pipeline_runs triggers a run

Configuration
- Optional: Run-level configuration for timeouts and default payload
