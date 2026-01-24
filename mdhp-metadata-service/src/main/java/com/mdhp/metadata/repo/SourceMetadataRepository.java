package com.mdhp.metadata.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mdhp.metadata.model.SourceMetadata;

public interface SourceMetadataRepository extends JpaRepository<SourceMetadata, Long> {
}
