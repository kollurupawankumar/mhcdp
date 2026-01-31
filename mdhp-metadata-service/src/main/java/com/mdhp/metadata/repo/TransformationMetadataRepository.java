package com.mdhp.metadata.repo;

import com.mdhp.metadata.model.TransformationMetadataEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


public interface TransformationMetadataRepository extends JpaRepository<TransformationMetadataEntity, Long> {
}
