package com.opencode.release.model;


import java.util.List;
import java.util.Map;

public record IngestionPipelineYaml(

        Ingestion ingestion,
        Source source,
        Target target,
        Transformation transformation,
        Schedule schedule,
        Compute compute,
        Governance governance,
        Retry retry,
        Extraction extraction,
        Incremental incremental,
        Landing landing,
        SchemaEvolution schemaEvolution,
        Classification classification,
        SLA sla

) {}

