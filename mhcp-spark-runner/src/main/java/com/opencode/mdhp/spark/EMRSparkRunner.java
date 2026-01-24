package com.opencode.mdhp.spark;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;

/**
 * EMR Spark Runner (Phase 7): placeholder implementation that can be wired to AWS EMR.
 * Local development continues to use the LocalSparkRunner. This class provides a clean
 * extension point for EMR integration without impacting the existing LOCAL flow.
 */
public class EMRSparkRunner implements SparkRunner {
  private static final Logger log = LoggerFactory.getLogger(EMRSparkRunner.class);

  @Override
  public SparkJobHandle runJob(String stage, String payload, String scriptPath, String inputPath, String outputPath) throws Exception {
    SparkJobHandle handle = new SparkJobHandle();
    handle.setStage(stage);
    handle.setRunId(java.util.UUID.randomUUID().toString());
    handle.setJobIdentifier(scriptPath);
    handle.setStartedAt(java.time.Instant.now());

    boolean emrEnabled = Boolean.parseBoolean(System.getProperty("emr.enabled", "false"));
    if (emrEnabled) {
      log.info("[EMR] Submitting EMR Spark job for stage {} using script {}", stage, scriptPath);
      // Placeholder: In Phase 7 we'd submit an EMR Step via AWS SDK and poll for completion
      // Simulate EMR latency
      Thread.sleep(2500);
    } else {
      log.info("EMR not enabled; executing EMR path as a simulated local run for stage {}", stage);
      Thread.sleep(1500);
    }

    handle.setEndedAt(java.time.Instant.now());
    handle.setStatus("COMPLETED");
    handle.setLogs("EMR path (simulation) completed");
    return handle;
  }
}
