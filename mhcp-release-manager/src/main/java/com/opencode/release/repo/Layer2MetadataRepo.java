package com.opencode.release.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class Layer2MetadataRepo {

    private final JdbcTemplate jdbc;

    public Layer2MetadataRepo(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // =========================================================
    // DOMAIN MASTER
    // =========================================================
    public void upsertDomain(
            String domainCode,
            String frequency) {

        jdbc.update("""
            INSERT INTO domain_master(
                domain_code,
                domain_name,
                run_frequency,
                active_flag,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, TRUE, NOW(), NOW())
            ON CONFLICT (domain_code)
            DO UPDATE SET
                run_frequency = EXCLUDED.run_frequency,
                updated_at = NOW()
        """,
                domainCode,
                domainCode,
                frequency
        );
    }

    // =========================================================
    // ENTITY MASTER
    // =========================================================
    public void upsertEntity(
            String domainCode,
            String entityId,
            String frequency) {

        jdbc.update("""
            INSERT INTO entity_master(
                domain_code,
                entity_id,
                entity_name,
                entity_frequency,
                active_flag,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, ?, TRUE, NOW(), NOW())
            ON CONFLICT (entity_id)
            DO UPDATE SET
                entity_frequency = EXCLUDED.entity_frequency,
                updated_at = NOW()
        """,
                domainCode,
                entityId,
                entityId,
                frequency
        );
    }

    // =========================================================
    // SOURCE METADATA
    // =========================================================
    public void insertSourceMetadata(
            String domain,
            String entity,
            String sourceType,
            String jsonConfig,
            int version) {

        jdbc.update("""
            INSERT INTO source_metadata(
                domain_code,
                entity_id,
                source_type,
                source_config,
                version,
                active_flag,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, ?::jsonb, ?, TRUE, NOW(), NOW())
        """,
                domain,
                entity,
                sourceType,
                jsonConfig,
                version
        );
    }

    // =========================================================
    // TRANSFORMATION METADATA
    // =========================================================
    public void insertTransformationMetadata(
            String domain,
            String entity,
            String inputTable,
            String outputTable,
            String jsonConfig,
            int version) {

        jdbc.update("""
            INSERT INTO transformation_metadata(
                domain_code,
                entity_id,
                input_table,
                output_table,
                transform_config,
                version,
                active_flag,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?, ?, ?::jsonb, ?, TRUE, NOW(), NOW())
        """,
                domain,
                entity,
                inputTable,
                outputTable,
                jsonConfig,
                version
        );
    }

    // =========================================================
    // ENRICHMENT METADATA
    // =========================================================
    public void insertEnrichmentMetadata(
            String domain,
            String entity,
            String jsonConfig,
            int version) {

        jdbc.update("""
            INSERT INTO enrichment_metadata(
                domain_code,
                entity_id,
                enrich_config,
                version,
                active_flag,
                created_at,
                updated_at
            )
            VALUES (?, ?, ?::jsonb, ?, TRUE, NOW(), NOW())
        """,
                domain,
                entity,
                jsonConfig,
                version
        );
    }

    // =========================================================
    // OPTIONAL — DEACTIVATE OLD VERSIONS
    // (useful during activation step)
    // =========================================================
    public void deactivateOldSourceVersions(
            String entity) {

        jdbc.update("""
            UPDATE source_metadata
            SET active_flag = FALSE,
                updated_at = NOW()
            WHERE entity_id = ?
        """, entity);
    }

    public void deactivateOldTransformationVersions(
            String entity) {

        jdbc.update("""
            UPDATE transformation_metadata
            SET active_flag = FALSE,
                updated_at = NOW()
            WHERE entity_id = ?
        """, entity);
    }

    public void deactivateOldEnrichmentVersions(
            String entity) {

        jdbc.update("""
            UPDATE enrichment_metadata
            SET active_flag = FALSE,
                updated_at = NOW()
            WHERE entity_id = ?
        """, entity);
    }
}
