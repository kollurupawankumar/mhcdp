package com.opencode.mdhp.ingestion.model;


import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "pipeline_event_log")
public class PipelineEventLog {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "run_id")
  private String runId;

  @Column(name = "stage")
  private String stage;

  @Column(name = "status")
  private String status;

  @Column(name = "timestamp")
  private Instant timestamp;

  @Column(name = "job_identifier")
  private String jobIdentifier;

  @Column(name = "error_details")
  private String errorDetails;

  // Getters/Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getRunId() { return runId; }
  public void setRunId(String runId) { this.runId = runId; }
  public String getStage() { return stage; }
  public void setStage(String stage) { this.stage = stage; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Instant getTimestamp() { return timestamp; }
  public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
  public String getJobIdentifier() { return jobIdentifier; }
  public void setJobIdentifier(String jobIdentifier) { this.jobIdentifier = jobIdentifier; }
  public String getErrorDetails() { return errorDetails; }
  public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }
}
