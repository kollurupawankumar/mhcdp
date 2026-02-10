package com.opencode.release.service;

import com.opencode.release.model.IngestionPipelineYaml;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    // ---------------------------------------------------------
    // INGESTION YAML VALIDATION
    // ---------------------------------------------------------
    public void validateIngestionYaml(
            IngestionPipelineYaml yaml) {

        if (yaml.ingestion() == null)
            throw new IllegalArgumentException(
                    "Missing ingestion section");

        if (yaml.ingestion().pipeline_name() == null
                || yaml.ingestion().pipeline_name().isBlank())
            throw new IllegalArgumentException(
                    "pipeline_name is mandatory");

        if (yaml.ingestion().domain() == null)
            throw new IllegalArgumentException(
                    "domain is mandatory");

        if (yaml.ingestion().entity() == null)
            throw new IllegalArgumentException(
                    "entity is mandatory");

        if (yaml.source() == null)
            throw new IllegalArgumentException(
                    "source section missing");

        if (yaml.target() == null)
            throw new IllegalArgumentException(
                    "target section missing");

        validateSource(yaml);
    }

    // ---------------------------------------------------------
    // SOURCE VALIDATION
    // ---------------------------------------------------------
    private void validateSource(
            IngestionPipelineYaml yaml) {

        String type =
                yaml.source().type();

        if (type == null)
            throw new IllegalArgumentException(
                    "source.type is mandatory");

        switch (type.toLowerCase()) {

            case "oracle":
            case "postgres":
            case "mysql":
            case "snowflake":

                if (yaml.source().table() == null)
                    throw new IllegalArgumentException(
                            "DB source requires table");

                break;

            case "file":

                if (yaml.source().path() == null)
                    throw new IllegalArgumentException(
                            "File source requires path");

                break;

            case "api":

                if (yaml.source().endpoint() == null)
                    throw new IllegalArgumentException(
                            "API source requires endpoint");

                break;

            case "kafka":

                if (yaml.source().topic() == null)
                    throw new IllegalArgumentException(
                            "Kafka source requires topic");

                break;

            default:
                throw new IllegalArgumentException(
                        "Unsupported source type: "
                                + type);
        }
    }
}
