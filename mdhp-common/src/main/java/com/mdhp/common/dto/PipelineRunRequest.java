package com.mdhp.common.dto;

public record PipelineRunRequest(
        String domainCode,
        String entityId,
        Object payload
) {}
