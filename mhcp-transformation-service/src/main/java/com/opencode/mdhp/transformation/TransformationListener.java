package com.opencode.mdhp.transformation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.opencode.mdhp.ingestion.model.PipelineEventLog;
import com.opencode.mdhp.ingestion.repo.PipelineEventLogRepository;
import com.opencode.mdhp.ingestion.model.JobCompletionOutbox;
import com.opencode.mdhp.ingestion.repo.JobCompletionOutboxRepository;
import java.time.Instant;

import com.opencode.mdhp.spark.SparkRunner;
import com.opencode.mdhp.spark.SparkRunnerFactory;
import com.opencode.mdhp.spark.SparkJobHandle;

@Component
public class TransformationListener {
  private static final Logger log = LoggerFactory.getLogger(TransformationListener.class);

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private PipelineEventLogRepository eventRepo;

  @Autowired
  private JobCompletionOutboxRepository outboxRepo;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "mdhp.pipeline.transformation.request", groupId = "mdhp-transform")
  public void onTransformationRequest(String message) {
    log.info("Transformation requested: {}", message);
    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

    // Derive runId and payload
    String runId = null;
    String payload = message;
    try {
      JsonNode root = objectMapper.readTree(message);
      if (root.has("runId")) runId = root.get("runId").asText();
      if (root.has("payload")) payload = root.get("payload").toString();
    } catch (Exception e) {
      runId = java.util.UUID.randomUUID().toString();
    }
    if (runId == null || runId.isEmpty()) runId = java.util.UUID.randomUUID().toString();

    String stage = "TRANSFORMATION";

    // Spark config with potential overrides from UI metadata payload
    String scriptPath = System.getProperty("spark.script.transformation", "/path/to/transform.py");
    String inputPath = System.getProperty("spark.input.path", "/data/transform/input");
    String outputPath = System.getProperty("spark.output.path", "/data/transform/output");
    try {
      JsonNode root = objectMapper.readTree(message);
      if (root.has("inputPath")) inputPath = root.get("inputPath").asText();
      if (root.has("outputPath")) outputPath = root.get("outputPath").asText();
      if (root.has("scriptPath")) scriptPath = root.get("scriptPath").asText();
    } catch (Exception ignore) { }

    String runnerType = System.getProperty("spark.runner.type", "LOCAL");
    SparkRunner runner = SparkRunnerFactory.getRunner(SparkRunnerFactory.RunnerType.valueOf(runnerType));
    SparkJobHandle sparkHandle = null;
    try {
      sparkHandle = runner.runJob(stage, payload, scriptPath, inputPath, outputPath);
      if (sparkHandle != null) {
        sparkHandle.setRunId(runId);
        sparkHandle.setStage(stage);
        sparkHandle.setJobIdentifier(scriptPath);
      }
    } catch (Exception ex) {
      log.error("Spark transformation failed for run {} stage {}: {}", runId, stage, ex.getMessage());
      String failFast = System.getProperty("spark.runner.failFast", "true");
      if (Boolean.parseBoolean(failFast)) {
        throw new RuntimeException(ex);
      } else {
        // mark outbox and publish failed
        JobCompletionOutbox outbox = new JobCompletionOutbox();
        outbox.setRunId(runId); outbox.setStage(stage); outbox.setTerminalState("FAILED"); outbox.setEventPayload(payload);
        outboxRepo.save(outbox);
        kafkaTemplate.send("mdhp.pipeline.transformation.failed", runId, payload);
        return;
      }
    }

    // Success path: mark outbox and publish completed
    JobCompletionOutbox outbox = new JobCompletionOutbox();
    outbox.setRunId(runId); outbox.setStage(stage); outbox.setTerminalState("SENT"); outbox.setEventPayload(payload);
    outboxRepo.save(outbox);
    kafkaTemplate.send("mdhp.pipeline.transformation.completed", runId, payload);
  }
}
