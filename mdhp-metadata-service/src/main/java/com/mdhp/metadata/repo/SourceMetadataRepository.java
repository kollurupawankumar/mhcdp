package com.mdhp.metadata.repo;

import com.mdhp.metadata.model.SourceMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



public interface SourceMetadataRepository extends JpaRepository<SourceMetadataEntity, Long> {
}
