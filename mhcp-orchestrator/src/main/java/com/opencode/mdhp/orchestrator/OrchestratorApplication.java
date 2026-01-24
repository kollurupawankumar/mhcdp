package com.opencode.mdhp.orchestrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import java.util.UUID;

@SpringBootApplication
@RestController
public class OrchestratorApplication {
  @Autowired
  private KafkaTemplate<String, String> kafkaTemplate;

  public static void main(String[] args) {
    SpringApplication.run(OrchestratorApplication.class, args);
  }

  @PostMapping("/pipeline_runs")
  public String triggerPipeline(@RequestBody String payload) {
    // Create a deterministic runId and wrap payload with runId for downstream consumers
    String runId = UUID.randomUUID().toString();
    String event = "{\"runId\":\"" + runId + "\",\"payload\":" + payload + "}";
    String topic = "mdhp.pipeline.ingestion.request";
    kafkaTemplate.send(topic, runId, event);
    return runId;
  }
}
