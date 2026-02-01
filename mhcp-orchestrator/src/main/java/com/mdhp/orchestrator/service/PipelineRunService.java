package com.mdhp.orchestrator.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mdhp.common.dto.PipelineRunRequest;

import com.mdhp.orchestrator.entity.PipelineOutboxEventEntity;
import com.mdhp.orchestrator.entity.PipelineRunEntity;
import com.mdhp.orchestrator.repository.PipelineOutboxEventRepository;
import com.mdhp.orchestrator.repository.PipelineRunRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PipelineRunService {

    private static final String TOPIC_INGESTION_REQUEST = "mdhp.pipeline.ingestion.request";

    private final PipelineRunRepository runRepo;
    private final PipelineOutboxEventRepository outboxRepo;
    private final ObjectMapper objectMapper;

    public PipelineRunService(PipelineRunRepository runRepo,
                              PipelineOutboxEventRepository outboxRepo,
                              ObjectMapper objectMapper) {
        this.runRepo = runRepo;
        this.outboxRepo = outboxRepo;
        this.objectMapper = objectMapper;
    }

    private String generateRunId(String domainCode) {
        String date = LocalDate.now(ZoneId.of("Asia/Kolkata"))
                .format(DateTimeFormatter.BASIC_ISO_DATE); // 20260131

        String rand = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();

        return "RUN-" + date + "-" + domainCode.toUpperCase() + "-" + rand;
    }

    @Transactional
    public String createPipelineRun(PipelineRunRequest request) throws Exception {
        String runId = generateRunId(request.domainCode());

        PipelineRunEntity run = new PipelineRunEntity();
        run.setRunId(runId);
        run.setDomainCode(request.domainCode());
        run.setEntityId(request.entityId());
        run.setStatus("RUNNING");
        runRepo.save(run);

        Map<String, Object> event = new LinkedHashMap<>();
        event.put("runId", runId);
        event.put("domainCode", request.domainCode());
        event.put("entityId", request.entityId());

        String eventJson = objectMapper.writeValueAsString(event);

        PipelineOutboxEventEntity outboxEvent =
                PipelineOutboxEventEntity.newEvent(runId, TOPIC_INGESTION_REQUEST, "PIPELINE_TRIGGERED", eventJson);

        outboxRepo.save(outboxEvent);

        return runId;
    }
}

