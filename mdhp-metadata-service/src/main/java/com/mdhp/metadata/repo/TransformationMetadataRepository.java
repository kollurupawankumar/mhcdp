package com.mdhp.metadata.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mdhp.metadata.model.TransformationMetadata;

public interface TransformationMetadataRepository extends JpaRepository<TransformationMetadata, Long> {
}
