package com.opencode.release.model;

public record Ingestion(

        String pipeline_name,
        String domain,
        String entity,
        String load_type,        // full | incremental
        String orchestration     // airflow | argo | adf

) {}
