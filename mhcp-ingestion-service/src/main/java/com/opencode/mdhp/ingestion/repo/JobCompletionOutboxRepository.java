package com.opencode.mdhp.ingestion.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import com.opencode.mdhp.ingestion.model.JobCompletionOutbox;

public interface JobCompletionOutboxRepository extends JpaRepository<JobCompletionOutbox, Long> {
}
