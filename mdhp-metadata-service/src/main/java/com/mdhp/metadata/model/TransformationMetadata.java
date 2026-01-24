package com.mdhp.metadata.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transformation_metadata")
public class TransformationMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id")
  private String entityId;

  @Column(name = "input_table")
  private String inputTable;

  @Column(name = "output_table")
  private String outputTable;
  @Column(name = "input_path", length = 512)
  private String inputPath;
  @Column(name = "output_path", length = 512)
  private String outputPath;

  @Column(name = "transform_config", columnDefinition = "jsonb")
  private String transformConfig;

  private Integer version;

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
  public String getInputTable() { return inputTable; }
  public void setInputTable(String inputTable) { this.inputTable = inputTable; }
  public String getOutputTable() { return outputTable; }
  public void setOutputTable(String outputTable) { this.outputTable = outputTable; }
  public String getInputPath() { return inputPath; }
  public void setInputPath(String inputPath) { this.inputPath = inputPath; }
  public String getOutputPath() { return outputPath; }
  public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
  public String getTransformConfig() { return transformConfig; }
  public void setTransformConfig(String transformConfig) { this.transformConfig = transformConfig; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public Boolean getActiveFlag() { return activeFlag; }
  public void setActiveFlag(Boolean activeFlag) { this.activeFlag = activeFlag; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
