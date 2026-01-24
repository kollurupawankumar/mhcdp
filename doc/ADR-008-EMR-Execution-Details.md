ADR 008: EMR Execution Details

- Intent: Document how EMR execution will be performed in Phase 7.
- Scope: EMR API usage, step submission, polling, idempotent handling, and error handling.
- Decision: Use AWS EMR SDK to submit PySpark steps; implement a small wrapper to reuse existing SparkRunner interface; add config keys for region, clusterId, steps, timeouts.
- Status: Proposed
