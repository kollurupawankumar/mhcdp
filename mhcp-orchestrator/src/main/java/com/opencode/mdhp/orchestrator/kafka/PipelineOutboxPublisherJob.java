package com.opencode.mdhp.orchestrator.kafka;


import com.mdhp.common.entity.PipelineOutboxEventEntity;
import com.mdhp.common.repository.PipelineOutboxEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PipelineOutboxPublisherJob {

    private static final Logger log = LoggerFactory.getLogger(PipelineOutboxPublisherJob.class);

    private final PipelineOutboxEventRepository outboxRepo;
    private final PipelineEventPublisher publisher;

    public PipelineOutboxPublisherJob(PipelineOutboxEventRepository outboxRepo,
                                      PipelineEventPublisher publisher) {
        this.outboxRepo = outboxRepo;
        this.publisher = publisher;
    }

    @Scheduled(fixedDelayString = "${outbox.publisher.delay-ms:2000}")
    @Transactional
    public void publishPendingEvents() {
        List<PipelineOutboxEventEntity> events =
                outboxRepo.findNewEvents(PageRequest.of(0, 50));

        if (events.isEmpty()) return;

        for (PipelineOutboxEventEntity e : events) {
            try {
                publisher.publish(e.getTopic(), e.getRunId(), e.getPayload());

                // ✅ only if publish succeeded
                e.markSent();

            } catch (Exception ex) {
                log.error("Outbox publish failed: eventId={} runId={}",
                        e.getEventId(), e.getRunId(), ex);

                e.markFailed(ex.getMessage());
            }

            outboxRepo.save(e);
        }
    }
}
