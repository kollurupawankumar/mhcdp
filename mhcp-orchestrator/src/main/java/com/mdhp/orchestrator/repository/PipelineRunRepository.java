package com.mdhp.orchestrator.repository;

import com.mdhp.orchestrator.entity.PipelineRunEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PipelineRunRepository extends JpaRepository<PipelineRunEntity, Long> {
    Optional<PipelineRunEntity> findByRunId(String runId);
}
