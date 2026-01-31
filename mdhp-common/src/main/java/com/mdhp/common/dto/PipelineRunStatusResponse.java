package com.mdhp.common.dto;


import java.time.OffsetDateTime;

public record PipelineRunStatusResponse(
        String runId,
        String domainCode,
        String entityId,
        String status,
        OffsetDateTime startedAt,
        OffsetDateTime completedAt,
        OffsetDateTime createdAt
) {}
