package com.opencode.mdhp.ingestion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import java.time.Instant;

import com.opencode.mdhp.ingestion.repo.PipelineEventLogRepository;
import com.opencode.mdhp.ingestion.model.PipelineEventLog;
import com.opencode.mdhp.ingestion.repo.JobCompletionOutboxRepository;
import com.opencode.mdhp.spark.SparkRunner;
import com.opencode.mdhp.spark.SparkRunnerFactory;
import com.opencode.mdhp.spark.SparkJobHandle;
import com.opencode.mdhp.ingestion.model.JobCompletionOutbox;

@Component
public class IngestionListener {
  private static final Logger log = LoggerFactory.getLogger(IngestionListener.class);

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private PipelineEventLogRepository eventRepo;

  @Autowired
  private JobCompletionOutboxRepository outboxRepo;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @KafkaListener(topics = "mdhp.pipeline.ingestion.request", groupId = "mdhp-ingest")
  public void onIngestionRequest(String message) throws Exception {
    log.info("Ingestion requested: {}", message);
    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    // Build a structured payload including paths for Spark job
    // Parse runId and payload if provided
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

    String stage = "INGESTION";
    // Resolve Spark input/output/script paths from config or metadata (defaults provided as config)
    String scriptPath = System.getProperty("spark.script.ingestion", "/path/to/ingest.py");
    String inputPath = System.getProperty("spark.input.path", "/data/ingest/input");
    String outputPath = System.getProperty("spark.output.path", "/data/ingest/output");

    // Idempotency check
    if (eventRepo.findByRunIdAndStage(runId, stage) != null) {
      log.info("Ingestion for runId {} already processed. Skipping.", runId);
      return;
    }

    // Persist a log entry for this stage
    PipelineEventLog elog = new PipelineEventLog();
    elog.setRunId(runId);
    elog.setStage(stage);
    elog.setStatus("REQUESTED");
    elog.setTimestamp(Instant.now());
    eventRepo.save(elog);

    // Outbox entry before publish
    JobCompletionOutbox outbox = new JobCompletionOutbox();
    outbox.setRunId(runId);
    outbox.setStage(stage);
    outbox.setTerminalState("PENDING");
    outbox.setEventPayload(payload);
    outbox.setCreatedAt(Instant.now());
    outboxRepo.save(outbox);

    // Spark runner: choose type from system property (default LOCAL)
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
      log.error("Spark job failed for run {} stage {}: {}", runId, stage, ex.getMessage());
      String failFast = System.getProperty("spark.runner.failFast", "true");
      if (Boolean.parseBoolean(failFast)) {
        throw ex;
      } else {
        outbox.setTerminalState("FAILED"); outboxRepo.save(outbox);
        kafkaTemplate.send("mdhp.pipeline.ingestion.failed", runId, payload);
        return;
      }
    }

    // After successful run, mark outbox as SENT and publish completion
    outbox.setTerminalState("SENT"); outboxRepo.save(outbox);
    kafkaTemplate.send("mdhp.pipeline.ingestion.completed", runId, payload);
  }
}
