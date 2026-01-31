package com.mdhp.metadata.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "entity_master",
        indexes = {
                @Index(name = "idx_entity_domain_code", columnList = "domain_code"),
                @Index(name = "idx_entity_active", columnList = "active_flag")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_entity_master_entity_id", columnNames = {"entity_id"})
        }
)
public class EntityMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_code", nullable = false, length = 100)
    private String domainCode;

    @Column(name = "entity_id", nullable = false, length = 255, unique = true)
    private String entityId;

    @Column(name = "subject_area", length = 255)
    private String subjectArea;

    @Column(name = "entity_name", nullable = false, length = 255)
    private String entityName;

    @Column(name = "entity_description", columnDefinition = "text")
    private String entityDescription;

    @Column(name = "entity_frequency", length = 50)
    private String entityFrequency; // override

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

    public String getSubjectArea() {
        return subjectArea;
    }

    public void setSubjectArea(String subjectArea) {
        this.subjectArea = subjectArea;
    }

    public String getEntityName() {
        return entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    public String getEntityDescription() {
        return entityDescription;
    }

    public void setEntityDescription(String entityDescription) {
        this.entityDescription = entityDescription;
    }

    public String getEntityFrequency() {
        return entityFrequency;
    }

    public void setEntityFrequency(String entityFrequency) {
        this.entityFrequency = entityFrequency;
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
