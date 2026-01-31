package com.mdhp.metadata.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;


@Entity
@Table(
        name = "source_metadata",
        indexes = {
                @Index(name = "idx_source_entity", columnList = "entity_id"),
                @Index(name = "idx_source_domain", columnList = "domain_code"),
                @Index(name = "idx_source_active", columnList = "active_flag")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_source_entity_version", columnNames = {"entity_id", "version"})
        }
)
public class SourceMetadataEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "domain_code", nullable = false, length = 100)
  private String domainCode;

  @Column(name = "entity_id", nullable = false, length = 255)
  private String entityId;

  @Column(name = "source_type", nullable = false, length = 50)
  private String sourceType;

  @JdbcTypeCode(SqlTypes.JSON)
  @Column(name = "source_config", nullable = false, columnDefinition = "jsonb")
  private Map<String, Object> sourceConfig;


  @Column(name = "version", nullable = false)
  private Integer version = 1;


  @Column(name = "active_flag", nullable = false)
  private Boolean activeFlag = true;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private LocalDateTime updatedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getDomainCode() {
    return domainCode;
  }

  public void setDomainCode(String domainCode) {
    this.domainCode = domainCode;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public String getSourceType() {
    return sourceType;
  }

  public void setSourceType(String sourceType) {
    this.sourceType = sourceType;
  }

  public Map<String, Object> getSourceConfig() {
    return sourceConfig;
  }

  public void setSourceConfig(Map<String, Object> sourceConfig) {
    this.sourceConfig = sourceConfig;
  }

  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public Boolean getActiveFlag() {
    return activeFlag;
  }

  public void setActiveFlag(Boolean activeFlag) {
    this.activeFlag = activeFlag;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }
}
