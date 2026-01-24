ADR 004: Spark Runner Fail-Fast

- Intent: Make Spark runner fail-fast by default for local/dev and prod parity.
- Rationale: Predictable failure modes reduce debugging time and prevent silent retries.
- Consequences: When a Spark job fails and fail-fast is true, the pipeline halts for the current run; when false, the system records failure and continues with DLQ/pipeline failure flow.
- Decision: Implement SparkRunner with a system property spark.runner.failFast (default true) that controls whether exceptions are propagated to callers or handled gracefully via outbox and a failed event.
- Status: Accepted
