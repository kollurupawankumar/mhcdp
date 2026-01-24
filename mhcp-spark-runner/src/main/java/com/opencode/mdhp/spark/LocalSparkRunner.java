package com.opencode.mdhp.spark;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;

public class LocalSparkRunner implements SparkRunner {
  private static final Logger log = LoggerFactory.getLogger(LocalSparkRunner.class);

  private boolean failFast = true;

  private String sparkSubmitCmd = "spark-submit"; // default, could be overridden via config

  @Override
  public SparkJobHandle runJob(String stage, String payload, String scriptPath, String inputPath, String outputPath) throws Exception {
    SparkJobHandle handle = new SparkJobHandle();
    handle.setStage(stage);
    handle.setRunId(java.util.UUID.randomUUID().toString());
    handle.setJobIdentifier(inputPath + ":" + outputPath);
    handle.setStartedAt(Instant.now());

    // Build spark-submit command
    List<String> cmd = new ArrayList<>();
    cmd.add(sparkSubmitCmd);
    if (scriptPath != null && !scriptPath.isEmpty()) {
      cmd.add(scriptPath);
    }
    if (inputPath != null) {
      cmd.add("--input"); cmd.add(inputPath);
    }
    if (outputPath != null) {
      cmd.add("--output"); cmd.add(outputPath);
    }
    // payload can be passed as an argument if needed
    if (payload != null && !payload.isEmpty()) {
      cmd.add("--payload"); cmd.add(payload);
    }

    try {
      ProcessBuilder pb = new ProcessBuilder(cmd);
      pb.redirectErrorStream(true);
      Process p = pb.start();
      int exitCode = p.waitFor();
      handle.setEndedAt(Instant.now());
      if (exitCode == 0) {
        handle.setStatus("COMPLETED");
        handle.setLogs("spark-submit exited 0");
      } else {
        handle.setStatus("FAILED");
        handle.setLogs("spark-submit exit code " + exitCode);
        if (failFast) {
          throw new RuntimeException("Spark job failed with exit code " + exitCode);
        }
      }
    } catch (Exception ex) {
      handle.setEndedAt(Instant.now());
      handle.setStatus("FAILED");
      handle.setLogs(ex.getMessage());
      if (failFast) throw ex;
    }

    return handle;
  }
}
