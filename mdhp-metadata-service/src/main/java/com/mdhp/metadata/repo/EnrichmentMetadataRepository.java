package com.mdhp.metadata.repo;

import com.mdhp.metadata.model.EnrichmentMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface EnrichmentMetadataRepository extends JpaRepository<EnrichmentMetadataEntity, Long> {
}
