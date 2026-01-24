package com.opencode.mdhp.spark;

import java.time.Instant;

public class SparkJobHandle {
  private String runId;
  private String stage;
  private String jobIdentifier;
  private String status;
  private Instant startedAt;
  private Instant endedAt;
  private String logs;

  // Getters / Setters
  public String getRunId() { return runId; }
  public void setRunId(String runId) { this.runId = runId; }
  public String getStage() { return stage; }
  public void setStage(String stage) { this.stage = stage; }
  public String getJobIdentifier() { return jobIdentifier; }
  public void setJobIdentifier(String jobIdentifier) { this.jobIdentifier = jobIdentifier; }
  public String getStatus() { return status; }
  public void setStatus(String status) { this.status = status; }
  public Instant getStartedAt() { return startedAt; }
  public void setStartedAt(Instant startedAt) { this.startedAt = startedAt; }
  public Instant getEndedAt() { return endedAt; }
  public void setEndedAt(Instant endedAt) { this.endedAt = endedAt; }
  public String getLogs() { return logs; }
  public void setLogs(String logs) { this.logs = logs; }
}
