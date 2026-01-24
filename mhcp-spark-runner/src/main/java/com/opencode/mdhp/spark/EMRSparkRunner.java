package com.opencode.mdhp.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMRSparkRunner implements SparkRunner {
  private static final Logger log = LoggerFactory.getLogger(EMRSparkRunner.class);

  @Override
  public SparkJobHandle runJob(String stage, String payload, String scriptPath, String inputPath, String outputPath) throws Exception {
    // Placeholder EMR runner: simulate submission and completion
    SparkJobHandle handle = new SparkJobHandle();
    handle.setStage(stage);
    handle.setRunId(java.util.UUID.randomUUID().toString());
    handle.setJobIdentifier(scriptPath);
    handle.setStartedAt(java.time.Instant.now());
    log.info("[EMR] Submitting Spark job for stage {} with script {}", stage, scriptPath);
    // Simulate some latency
    Thread.sleep(1500);
    handle.setEndedAt(java.time.Instant.now());
    handle.setStatus("COMPLETED");
    handle.setLogs("EMR simulated run complete");
    return handle;
  }
}
