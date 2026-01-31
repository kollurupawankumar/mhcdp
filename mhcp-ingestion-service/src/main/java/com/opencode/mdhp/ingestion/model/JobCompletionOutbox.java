package com.opencode.mdhp.ingestion.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "job_completion_outbox")
public class JobCompletionOutbox {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "run_id")
  private String runId;

  @Column(name = "stage")
  private String stage;

  @Column(name = "terminal_state")
  private String terminalState;

  @Column(name = "event_payload", columnDefinition = "text")
  private String eventPayload;

  @Column(name = "created_at")
  private Instant createdAt;

  @Column(name = "updated_at")
  private Instant updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = Instant.now();
    updatedAt = Instant.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = Instant.now();
  }

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getRunId() { return runId; }
  public void setRunId(String runId) { this.runId = runId; }
  public String getStage() { return stage; }
  public void setStage(String stage) { this.stage = stage; }
  public String getTerminalState() { return terminalState; }
  public void setTerminalState(String terminalState) { this.terminalState = terminalState; }
  public String getEventPayload() { return eventPayload; }
  public void setEventPayload(String eventPayload) { this.eventPayload = eventPayload; }
  public Instant getCreatedAt() { return createdAt; }
  public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
  public Instant getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
