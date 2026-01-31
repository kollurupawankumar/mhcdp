package com.opencode.mdhp.spark;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.time.Instant;
import java.util.List;
import java.util.Arrays;

// EMR Serverless dependencies (added to pom.xml):
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.emrserverless.EmrServerlessClient;
import software.amazon.awssdk.services.emrserverless.model.*;

/** EMR Serverless Spark Runner (Phase 7). */
public class EMRSparkRunner implements SparkRunner {
  private static final Logger log = LoggerFactory.getLogger(EMRSparkRunner.class);
  private final ObjectMapper objectMapper = new ObjectMapper();


  @Override
  public SparkJobHandle runJob(String stage,
                               String payload,
                               String scriptPath,
                               String inputPath,
                               String outputPath) throws Exception {

    SparkJobHandle handle = new SparkJobHandle();
    handle.setStage(stage);
    handle.setRunId(java.util.UUID.randomUUID().toString());
    handle.setJobIdentifier(scriptPath);
    handle.setStartedAt(Instant.now());

    boolean emrEnabled = Boolean.parseBoolean(System.getProperty("emr.enabled", "false"));
    if (!emrEnabled) {
      // Fallback to local spark runner
      log.info("EMR not enabled; falling back to LOCAL Spark for stage {}", stage);
      return new LocalSparkRunner().runJob(stage, payload, scriptPath, inputPath, outputPath);
    }

    String region = System.getProperty("emr.region", "us-east-1");
    String appId = System.getProperty("emr.appId");
    String executionRole = System.getProperty("emr.executionRoleArn");
    String jobName = stage + "-" + handle.getRunId();

    if (appId == null || appId.isEmpty()) {
      throw new IllegalArgumentException("Missing system property: emr.appId");
    }
    if (executionRole == null || executionRole.isEmpty()) {
      throw new IllegalArgumentException("Missing system property: emr.executionRoleArn");
    }

    try (EmrServerlessClient client = EmrServerlessClient.builder()
            .region(Region.of(region))
            .build()) {

      // ✅ EMR Serverless uses SparkSubmit (NOT SparkSubmitJobDriver)
      SparkSubmit sparkSubmit = SparkSubmit.builder()
              .entryPoint(scriptPath) // must be s3://... in production, local path won't work in EMR
              .entryPointArguments(Arrays.asList(
                      "--stage", stage,
                      "--runId", handle.getRunId(),
                      "--input", inputPath,
                      "--output", outputPath
              ))
              // Optional: Spark submit parameters can be passed here
              // .sparkSubmitParameters("--conf spark.executor.instances=2 --conf spark.executor.memory=2g")
              .build();

      JobDriver jobDriver = JobDriver.builder()
              .sparkSubmit(sparkSubmit)
              .build();

      StartJobRunRequest req = StartJobRunRequest.builder()
              .applicationId(appId)
              .executionRoleArn(executionRole)
              .name(jobName)
              .jobDriver(jobDriver)
              .build();

      StartJobRunResponse resp = client.startJobRun(req);
      String jobRunId = resp.jobRunId();
      log.info("EMR Serverless started jobRunId {} for app {}", jobRunId, appId);

      // Poll for completion with timeout
      long start = System.currentTimeMillis();
      long timeoutMs = 10 * 60 * 1000; // 10 minutes
      GetJobRunRequest getReq = GetJobRunRequest.builder()
              .applicationId(appId)
              .jobRunId(jobRunId)
              .build();

      String finalState = "RUNNING";

      while (System.currentTimeMillis() - start < timeoutMs) {
        GetJobRunResponse getResp = client.getJobRun(getReq);
        finalState = getResp.jobRun().stateAsString();

        if ("SUCCESS".equalsIgnoreCase(finalState) || "SUCCEEDED".equalsIgnoreCase(finalState)) {
          break;
        }
        if ("FAILED".equalsIgnoreCase(finalState)
                || "CANCELLED".equalsIgnoreCase(finalState)
                || "CANCELLING".equalsIgnoreCase(finalState)) {
          break;
        }

        Thread.sleep(15000);
      }

      handle.setEndedAt(Instant.now());
      handle.setJobIdentifier(jobRunId);

      if ("SUCCESS".equalsIgnoreCase(finalState) || "SUCCEEDED".equalsIgnoreCase(finalState)) {
        handle.setStatus("COMPLETED");
        handle.setLogs("EMR Serverless job completed: state=" + finalState + ", jobRunId=" + jobRunId);
      } else {
        handle.setStatus("FAILED");
        handle.setLogs("EMR Serverless job failed or timed out: state=" + finalState + ", jobRunId=" + jobRunId);
      }

      return handle;

    } catch (Exception e) {
      log.error("EMR Serverless call failed: {}", e.getMessage(), e);

      // Fallback to LOCAL for resilience
      log.info("Falling back to LOCAL Spark for stage {}", stage);
      return new LocalSparkRunner().runJob(stage, payload, scriptPath, inputPath, outputPath);
    }
  }


}
