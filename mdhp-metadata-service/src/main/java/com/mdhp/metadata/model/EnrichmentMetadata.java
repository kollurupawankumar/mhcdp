package com.mdhp.metadata.model;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrichment_metadata")
public class EnrichmentMetadata {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "entity_id")
  private String entityId;

  @Column(name = "enrichment_mode")
  private String enrichmentMode;

  @Column(name = "sql_path")
  private String sqlPath;

  @Column(name = "sql_files")
  private String sqlFiles; // could be serialized array/string; placeholder for now

  @Column(name = "enrich_config", columnDefinition = "jsonb")
  private String enrichConfig;

  @Column(name = "input_path", length = 512)
  private String inputPath;
  @Column(name = "output_path", length = 512)
  private String outputPath;

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
  public String getEnrichmentMode() { return enrichmentMode; }
  public void setEnrichmentMode(String enrichmentMode) { this.enrichmentMode = enrichmentMode; }
  public String getSqlPath() { return sqlPath; }
  public void setSqlPath(String sqlPath) { this.sqlPath = sqlPath; }
  public String getSqlFiles() { return sqlFiles; }
  public void setSqlFiles(String sqlFiles) { this.sqlFiles = sqlFiles; }
  public String getEnrichConfig() { return enrichConfig; }
  public void setEnrichConfig(String enrichConfig) { this.enrichConfig = enrichConfig; }
  public String getInputPath() { return inputPath; }
  public void setInputPath(String inputPath) { this.inputPath = inputPath; }
  public String getOutputPath() { return outputPath; }
  public void setOutputPath(String outputPath) { this.outputPath = outputPath; }
  public Integer getVersion() { return version; }
  public void setVersion(Integer version) { this.version = version; }
  public Boolean getActiveFlag() { return activeFlag; }
  public void setActiveFlag(Boolean activeFlag) { this.activeFlag = activeFlag; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public LocalDateTime getUpdatedAt() { return updatedAt; }
  public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
