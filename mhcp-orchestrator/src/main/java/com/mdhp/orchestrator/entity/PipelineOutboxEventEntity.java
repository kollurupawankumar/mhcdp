package com.mdhp.orchestrator.entity;


import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "pipeline_outbox_event")
public class PipelineOutboxEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private UUID eventId;

    @Column(name = "run_id", nullable = false)
    private String runId;

    @Column(name = "topic", nullable = false)
    private String topic;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "payload", columnDefinition = "jsonb", nullable = false)
    private String payload;

    @Column(name = "status", nullable = false)
    private String status; // NEW/SENT/FAILED

    @Column(name = "attempts", nullable = false)
    private int attempts;

    @Column(name = "last_error")
    private String lastError;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (status == null) status = "NEW";
    }

    public static PipelineOutboxEventEntity newEvent(String runId, String topic, String eventType, String payload) {
        PipelineOutboxEventEntity e = new PipelineOutboxEventEntity();
        e.eventId = UUID.randomUUID();
        e.runId = runId;
        e.topic = topic;
        e.eventType = eventType;
        e.payload = payload;
        e.status = "NEW";
        e.attempts = 0;
        e.createdAt = OffsetDateTime.now();
        return e;
    }

    public void markSent() {
        this.status = "SENT";
        this.sentAt = OffsetDateTime.now();
        this.lastError = null;
    }

    public void markFailed(String err) {
        this.attempts++;
        this.lastError = err;
        if (this.attempts >= 10) {
            this.status = "FAILED";
        }
    }

    // getters/setters
    public Long getId() { return id; }
    public UUID getEventId() { return eventId; }
    public String getRunId() { return runId; }
    public String getTopic() { return topic; }
    public String getEventType() { return eventType; }
    public String getPayload() { return payload; }
    public String getStatus() { return status; }
    public int getAttempts() { return attempts; }
    public String getLastError() { return lastError; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getSentAt() { return sentAt; }

    public void setStatus(String status) { this.status = status; }
}
