package com.opencode.mdhp.ingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opencode.mdhp.ingestion.model.PipelineEventLog;

public interface PipelineEventLogRepository extends JpaRepository<PipelineEventLog, Long> {
  PipelineEventLog findByRunIdAndStage(String runId, String stage);
}
