package com.mdhp.metadata.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "source_metadata")
public class SourceMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id")
  private String entityId;

  @Column(name = "subject_area")
  private String subjectArea;

  @Column(name = "entity_name")
  private String entityName;

  @Column(name = "source_type")
  private String sourceType;

  @Column(name = "source_config", columnDefinition = "jsonb")
  private String sourceConfig;

  @Column(name = "input_path", length = 512)
  private String inputPath;

  @Column(name = "output_path", length = 512)
  private String outputPath;

  private String version;

  @Column(name = "active_flag")
  private Boolean activeFlag;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }

  // Getters & Setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getEntityId() { return entityId; }
  public void setEntityId(String entityId) { this.entityId = entityId; }
  public String getSubjectArea() { return subjectArea; }
  public void setSubjectArea(String subjectArea) { this.subjectArea = subjectArea; }
  public String getEntityName() { return entityName; }
  public void setEntityName(String entityName) { this.entityName = entityName; }
  public String getSourceType() { return sourceType; }
  public void setSourceType(String sourceType) { this.sourceType = sourceType; }
  public String getSourceConfig() { return sourceConfig; }
  public void setSourceConfig(String sourceConfig) { this.sourceConfig = sourceConfig; }
  public String getInputPath() { return inputPath; }
  public void setInputPath(String inputPath) { this.inputPath = inputPath; }
  public String getOutputPath() { return outputPath; }
  public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
  public String getVersion() { return version; }
  public void setVersion(String version) { this.version = version; }
  public Boolean getActiveFlag() { return activeFlag; }
  public void setActiveFlag(Boolean activeFlag) { this.activeFlag = activeFlag; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
