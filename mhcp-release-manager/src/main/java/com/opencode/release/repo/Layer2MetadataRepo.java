package com.opencode.release.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencode.release.compiler.CompiledEntityMetadata;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Layer2MetadataRepo {

    private final JdbcTemplate jdbc;
    private final ObjectMapper om;

    public Layer2MetadataRepo(JdbcTemplate jdbc, ObjectMapper om) {
        this.jdbc = jdbc;
        this.om = om;
    }

    // ----------------------------------------------------
    // DOMAIN MASTER
    // ----------------------------------------------------
    public void upsertDomain(String domainCode) {
        jdbc.update("""
            INSERT INTO domain_master(domain_code, domain_name, run_frequency, active_flag)
            VALUES (?, ?, ?, TRUE)
            ON CONFLICT (domain_code)
            DO UPDATE SET
                domain_name = EXCLUDED.domain_name,
                active_flag = TRUE,
                updated_at = now()
            """,
                domainCode,
                domainCode.toUpperCase(),
                "daily"
        );
    }

    // ----------------------------------------------------
    // ENTITY MASTER
    // ----------------------------------------------------
    public void upsertEntity(String domainCode, String entityId, String entityName) {
        jdbc.update("""
            INSERT INTO entity_master(domain_code, entity_id, subject_area, entity_name, active_flag)
            VALUES (?, ?, ?, ?, TRUE)
            ON CONFLICT (entity_id)
            DO UPDATE SET
                domain_code = EXCLUDED.domain_code,
                subject_area = EXCLUDED.subject_area,
                entity_name = EXCLUDED.entity_name,
                active_flag = TRUE,
                updated_at = now()
            """,
                domainCode,
                entityId,
                domainCode,
                entityName
        );
    }

    // ----------------------------------------------------
    // SOURCE METADATA
    // ----------------------------------------------------
    public void upsertSourceMetadata(CompiledEntityMetadata c, int version) {
        try {
            String sourceJson = om.writeValueAsString(c.sourceConfig());

            String sourceType = String.valueOf(
                    c.sourceConfig().getOrDefault("sourceType", "UNKNOWN")
            );

            jdbc.update("""
                INSERT INTO source_metadata(domain_code, entity_id, source_type, source_config, version, active_flag)
                VALUES (?, ?, ?, CAST(? AS jsonb), ?, TRUE)
                ON CONFLICT (entity_id, version)
                DO UPDATE SET
                    domain_code = EXCLUDED.domain_code,
                    source_type = EXCLUDED.source_type,
                    source_config = EXCLUDED.source_config,
                    active_flag = TRUE,
                    updated_at = now()
                """,
                    c.domainCode(),
                    c.entityId(),
                    sourceType,
                    sourceJson,
                    version
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert source_metadata for " + c.entityId(), e);
        }
    }

    // ----------------------------------------------------
    // TRANSFORMATION METADATA
    // ----------------------------------------------------
    public void upsertTransformationMetadata(CompiledEntityMetadata c, int version) {
        if (c.transformConfig() == null) return;

        try {
            String json = om.writeValueAsString(c.transformConfig());

            jdbc.update("""
                INSERT INTO transformation_metadata(domain_code, entity_id, transform_config, version, active_flag)
                VALUES (?, ?, CAST(? AS jsonb), ?, TRUE)
                ON CONFLICT (entity_id, version)
                DO UPDATE SET
                    domain_code = EXCLUDED.domain_code,
                    transform_config = EXCLUDED.transform_config,
                    active_flag = TRUE,
                    updated_at = now()
                """,
                    c.domainCode(),
                    c.entityId(),
                    json,
                    version
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert transformation_metadata for " + c.entityId(), e);
        }
    }

    // ----------------------------------------------------
    // ENRICHMENT METADATA
    // ----------------------------------------------------
    public void upsertEnrichmentMetadata(CompiledEntityMetadata c, int version) {
        if (c.enrichConfig() == null) return;

        try {
            String json = om.writeValueAsString(c.enrichConfig());

            jdbc.update("""
                INSERT INTO enrichment_metadata(domain_code, entity_id, enrich_config, version, active_flag)
                VALUES (?, ?, CAST(? AS jsonb), ?, TRUE)
                ON CONFLICT (entity_id, version)
                DO UPDATE SET
                    domain_code = EXCLUDED.domain_code,
                    enrich_config = EXCLUDED.enrich_config,
                    active_flag = TRUE,
                    updated_at = now()
                """,
                    c.domainCode(),
                    c.entityId(),
                    json,
                    version
            );

        } catch (Exception e) {
            throw new RuntimeException("Failed to upsert enrichment_metadata for " + c.entityId(), e);
        }
    }

    // ----------------------------------------------------
    // ACTIVATION HELPERS
    // ----------------------------------------------------
    public void deactivateOldVersions(String entityId, int keepVersion) {

        jdbc.update("""
            UPDATE source_metadata
            SET active_flag = FALSE, updated_at = now()
            WHERE entity_id = ?
              AND version <> ?
            """, entityId, keepVersion);

        jdbc.update("""
            UPDATE transformation_metadata
            SET active_flag = FALSE, updated_at = now()
            WHERE entity_id = ?
              AND version <> ?
            """, entityId, keepVersion);

        jdbc.update("""
            UPDATE enrichment_metadata
            SET active_flag = FALSE, updated_at = now()
            WHERE entity_id = ?
              AND version <> ?
            """, entityId, keepVersion);
    }
}
