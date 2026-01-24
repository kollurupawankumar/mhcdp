 ADR 006: EMR Runner Plan (Phase 7)

- Intent: Outline the plan to wire a production EMR Spark runner.
- Approach: Implement EMRSparkRunner with AWS EMR SDK; add config for region, clusterId, and Step definitions; expose via SparkRunnerFactory.
- Risks: Credential management and EMR cost; mitigation via IAM roles and environment gating.
- Status: Planned for Phase 7
- Implementation details:
- Implement EMRSparkRunner using AWS EMR SDK in Phase 7; provide a minimal EMR client wrapper.
- Add configuration for: emr.region, emr.clusterId, emr.iamRole, emr.steps, emr.timeouts.
- Wire EMR runner into SparkRunnerFactory with spark.runner.type=EMR.
- Provide a fallback to local simulation if AWS credentials are absent.
- Ensure outbox/reconciler continues to work with EMR flow.
