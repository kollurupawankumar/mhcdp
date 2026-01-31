package com.mdhp.common.repository;

import com.mdhp.common.entity.PipelineRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PipelineRunRepository extends JpaRepository<PipelineRunEntity, Long> {
    Optional<PipelineRunEntity> findByRunId(String runId);
}
