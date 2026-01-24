package com.opencode.mdhp.reconciler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.opencode.mdhp.ingestion.repo.JobCompletionOutboxRepository;
import com.opencode.mdhp.ingestion.repo.PipelineEventLogRepository;
import com.opencode.mdhp.ingestion.model.JobCompletionOutbox;

@Component
public class OutboxReconciler {
  private static final Logger log = LoggerFactory.getLogger(OutboxReconciler.class);

  @Autowired
  private JobCompletionOutboxRepository outboxRepo;

  @Autowired
  private com.opencode.mdhp.ingestion.repo.PipelineEventLogRepository eventRepo;

  // Very simple reconciler: every 60 seconds scan for PENDING outbox entries and log a note.
  @Scheduled(fixedDelay = 60000)
  public void reconcile() {
    // This is a placeholder: in a full implementation we'd republish missing events and update statuses.
    long count = outboxRepo.findAll().stream().filter(o -> "PENDING".equals(o.getTerminalState())).count();
    if (count > 0) {
      log.info("Reconciler found {} pending outbox entries (placeholder action)", count);
    }
  }
}
