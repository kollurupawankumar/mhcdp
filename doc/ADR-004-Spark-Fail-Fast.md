ADR 004: Spark Runner Fail-Fast

- Intent: Make Spark runner fail-fast by default in local/dev and prod parity.
- Rationale: Predictable failure improves debugging and reduces silent retries.
- Consequences: If Spark job fails and fail-fast is true, the pipeline for the run halts at that stage.
- Decision: Implement a SparkRunner flag spark.runner.failFast (default true). If a Spark job fails and fail-fast is true, propagate the exception; otherwise, record failure in outbox and emit a FAILED event.
- Status: Accepted
