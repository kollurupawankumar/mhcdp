package com.mdhp.metadata.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mdhp.metadata.model.EnrichmentMetadata;

public interface EnrichmentMetadataRepository extends JpaRepository<EnrichmentMetadata, Long> {
}
