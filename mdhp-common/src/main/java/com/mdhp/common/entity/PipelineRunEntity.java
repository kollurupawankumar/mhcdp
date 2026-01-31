package com.mdhp.common.entity;



import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "pipeline_run")
public class PipelineRunEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "run_id", nullable = false, unique = true)
    private String runId;

    @Column(name = "domain_code", nullable = false, length = 100)
    private String domainCode;

    @Column(name = "entity_id", nullable = false)
    private String entityId;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "status", nullable = false, length = 50)
    private String status; // RUNNING/SUCCESS/FAILED

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (createdAt == null) createdAt = OffsetDateTime.now();
        if (startedAt == null) startedAt = OffsetDateTime.now();
    }

    // getters + setters
    public Long getId() { return id; }

    public String getRunId() { return runId; }
    public void setRunId(String runId) { this.runId = runId; }

    public String getDomainCode() { return domainCode; }
    public void setDomainCode(String domainCode) { this.domainCode = domainCode; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public OffsetDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(OffsetDateTime startedAt) { this.startedAt = startedAt; }

    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
