package com.opencode.mdhp.enrichment;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.kafka.annotation.KafkaListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import com.opencode.mdhp.spark.SparkRunner;
import com.opencode.mdhp.spark.SparkRunnerFactory;
import com.opencode.mdhp.spark.SparkJobHandle;
import com.opencode.mdhp.ingestion.repo.PipelineEventLogRepository;
import com.opencode.mdhp.ingestion.repo.JobCompletionOutboxRepository;
import com.opencode.mdhp.ingestion.model.PipelineEventLog;
import com.opencode.mdhp.ingestion.model.JobCompletionOutbox;
import java.time.Instant;

@Component
public class EnrichmentListener {
  private static final Logger log = LoggerFactory.getLogger(EnrichmentListener.class);

  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  @Autowired
  private PipelineEventLogRepository eventRepo;

  @Autowired
  private JobCompletionOutboxRepository outboxRepo;

  @Override
  public String toString() { return super.toString(); }

  @KafkaListener(topics = "mdhp.pipeline.enrichment.request", groupId = "mdhp-enrich")
  public void onEnrichmentRequest(String message) throws Exception {
    log.info("Enrichment requested: {}", message);
    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

    // Parse runId and payload
    String runId = null;
    String payload = message;
    ObjectMapper mapper = new ObjectMapper();
    try {
      JsonNode root = mapper.readTree(message);
      if (root.has("runId")) runId = root.get("runId").asText();
      if (root.has("payload")) payload = root.get("payload").toString();
    } catch (Exception ignored) { }

    if (runId == null || runId.isEmpty()) runId = java.util.UUID.randomUUID().toString();
    String stage = "ENRICHMENT";

    // Idempotency check
    if (eventRepo.findByRunIdAndStage(runId, stage) != null) {
      log.info("Enrichment for runId {} already processed. Skipping.", runId);
      return;
    }

    // Persist a log entry
    PipelineEventLog elog = new PipelineEventLog();
    elog.setRunId(runId); elog.setStage(stage); elog.setStatus("REQUESTED"); elog.setTimestamp(Instant.now());
    eventRepo.save(elog);

    // Outbox before publish
    JobCompletionOutbox outbox = new JobCompletionOutbox();
    outbox.setRunId(runId); outbox.setStage(stage); outbox.setTerminalState("PENDING");
    outbox.setEventPayload(payload); outbox.setCreatedAt(Instant.now());
    outboxRepo.save(outbox);

    // Spark runner: fail-fast by default
    SparkRunner runner = SparkRunnerFactory.getRunner(SparkRunnerFactory.RunnerType.LOCAL);
    try {
      SparkJobHandle sparkHandle = runner.runJob(stage, payload, System.getProperty("spark.script.enrichment", "/path/to/enrich.py"), System.getProperty("spark.input.path", "/data/enrich/input"), System.getProperty("spark.output.path", "/data/enrich/output"));
      if (sparkHandle != null) {
        sparkHandle.setRunId(runId);
        sparkHandle.setStage(stage);
        sparkHandle.setJobIdentifier(System.getProperty("spark.script.enrichment", "/path/to/enrich.py"));
      }
    } catch (Exception ex) {
      log.error("Spark enrichment failed for run {} stage {}: {}", runId, stage, ex.getMessage());
      boolean failFast = Boolean.parseBoolean(System.getProperty("spark.runner.failFast", "true"));
      if (failFast) {
        throw ex;
      } else {
        outbox.setTerminalState("FAILED"); outboxRepo.save(outbox);
        kafkaTemplate.send("mdhp.pipeline.enrichment.failed", runId, payload);
        return;
      }
    }

    // Success path
    outbox.setTerminalState("SENT"); outboxRepo.save(outbox);
    kafkaTemplate.send("mdhp.pipeline.enrichment.completed", runId, payload);
  }
}
