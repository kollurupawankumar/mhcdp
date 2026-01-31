package com.mdhp.metadata.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "domain_master",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_domain_master_domain_code", columnNames = {"domain_code"})
        }
)
public class DomainMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "domain_code", nullable = false, length = 100, unique = true)
    private String domainCode;

    @Column(name = "domain_name", nullable = false, length = 255)
    private String domainName;

    @Column(name = "run_frequency", nullable = false, length = 50)
    private String runFrequency; // hourly/daily/weekly/monthly

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

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getRunFrequency() {
        return runFrequency;
    }

    public void setRunFrequency(String runFrequency) {
        this.runFrequency = runFrequency;
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
