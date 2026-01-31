package com.mdhp.metadata.repo;

import com.mdhp.metadata.model.DomainMasterEntity;
import com.mdhp.metadata.model.EnrichmentMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface DomainMasterRepository extends JpaRepository<DomainMasterEntity, Long> {

    Optional<DomainMasterEntity> findByDomainCode(String domainCode);

    boolean existsByDomainCode(String domainCode);
}
