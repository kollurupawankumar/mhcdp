package com.opencode.mdhp.spark;

public interface SparkRunner {
  SparkJobHandle runJob(String stage, String payload, String scriptPath, String inputPath, String outputPath) throws Exception;
}
