package com.opencode.release.compiler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencode.release.model.IngestionPipelineYaml;
import com.opencode.release.repo.Layer2MetadataRepo;
import org.springframework.stereotype.Component;

@Component
public class MetadataCompiler {

    private final Layer2MetadataRepo repo;
    private final ObjectMapper mapper;

    public MetadataCompiler(
            Layer2MetadataRepo repo,
            ObjectMapper mapper) {

        this.repo = repo;
        this.mapper = mapper;
    }

    // ---------------------------------------------------------
    // COMPILE INGESTION → LAYER 2
    // ---------------------------------------------------------
    public void compile(
            IngestionPipelineYaml yaml,
            int version) {

        try {

            String domain =
                    yaml.ingestion().domain();

            String entity =
                    yaml.ingestion().entity();

            String frequency =
                    yaml.schedule() != null
                            ? yaml.schedule().frequency()
                            : "daily";

            // DOMAIN
            repo.upsertDomain(
                    domain,
                    frequency
            );

            // ENTITY
            repo.upsertEntity(
                    domain,
                    entity,
                    frequency
            );

            // SOURCE METADATA
            String sourceJson =
                    mapper.writeValueAsString(yaml);

            repo.insertSourceMetadata(
                    domain,
                    entity,
                    yaml.source().type(),
                    sourceJson,
                    version
            );

            // TRANSFORMATION
            if (yaml.transformation() != null) {

                String transformJson =
                        mapper.writeValueAsString(
                                yaml.transformation()
                        );

                repo.insertTransformationMetadata(
                        domain,
                        entity,
                        yaml.source().table(),
                        yaml.target().table(),
                        transformJson,
                        version
                );
            }

            // ENRICHMENT / GOVERNANCE
            if (yaml.governance() != null) {

                String enrichJson =
                        mapper.writeValueAsString(
                                yaml.governance()
                        );

                repo.insertEnrichmentMetadata(
                        domain,
                        entity,
                        enrichJson,
                        version
                );
            }

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed compiling ingestion metadata",
                    e
            );
        }
    }
}
