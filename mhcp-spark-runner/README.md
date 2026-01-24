# MDHP Spark Runner

- Provides a SparkRunner interface and a LocalSparkRunner implementation.
- Fail-fast toggle: spark runner will throw on failure if configured to fail fast.
- Input/output script paths are configurable via method parameters and/or config.

Usage notes:
- Use SparkRunnerFactory.getRunner(SparkRunnerFactory.RunnerType.LOCAL) to obtain a runner.
- Call runJob(stage, payload, scriptPath, inputPath, outputPath) to execute.
- In production, switch to EMR runner by implementing an EMR runner and wiring SparkRunnerFactory accordingly.
