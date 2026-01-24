ADR 006: EMR Spark Runner Plan

- Intent: Provide a pluggable EMR runner to execute PySpark jobs in production, while keeping LOCAL runner in development.
- Rationale: EMR is the production compute path; LOCAL is for fast local iteration. A clean separation via a SparkRunner abstraction avoids code churn.
- Approach: Implement EMRSparkRunner with AWS EMR SDK hooks in Phase 7, add a flag to SparkRunnerFactory to switch runners, and supply configuration (region, clusterId, step definitions) via environment or UI metadata.
- Risks: AWS credentials, IAM permissions, EMR cluster availability, credential rotation. Mitigation: use IAM roles, parameter store, and environment-based switching.
- Status: Proposed; to be implemented in Phase 7.
