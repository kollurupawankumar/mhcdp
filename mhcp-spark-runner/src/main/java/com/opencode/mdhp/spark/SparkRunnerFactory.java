package com.opencode.mdhp.spark;

public class SparkRunnerFactory {
  public enum RunnerType { LOCAL, EMR }

  public static SparkRunner getRunner(RunnerType type) {
    if (type == RunnerType.LOCAL) {
      return new LocalSparkRunner();
    } else if (type == RunnerType.EMR) {
      return new EMRSparkRunner();
    }
    throw new UnsupportedOperationException("Unknown Spark runner type: " + type);
  }
}
