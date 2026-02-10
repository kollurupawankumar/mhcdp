package com.opencode.release.repo;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencode.release.util.ChecksumUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;


@Repository
public class MetadataRepo {


    private final JdbcTemplate jdbc;
    private final ObjectMapper jsonMapper;


    public MetadataRepo(JdbcTemplate jdbc, ObjectMapper jsonMapper) {
        this.jdbc = jdbc;
        this.jsonMapper = jsonMapper;
    }


    public void createRelease(String releaseId, String env, String gitCommit) {
        jdbc.update("""
                INSERT INTO metadata_release(release_id, env, git_commit, status)
                VALUES (?, ?, ?, 'CREATED')
                ON CONFLICT (release_id) DO NOTHING
                """, releaseId, env, gitCommit);
    }


    public void markReleaseActive(String releaseId) {
        jdbc.update("""
                UPDATE metadata_release
                SET status='ACTIVE'
                WHERE release_id=?
                """, releaseId);
    }


    public void addReleaseItem(String releaseId, String type, String name, int version) {
        jdbc.update("""
                INSERT INTO metadata_release_item(release_id, object_type, object_name, version)
                VALUES (?, ?, ?, ?)
                ON CONFLICT DO NOTHING
                """, releaseId, type, name, version);
    }


    public void upsertObject(String type, String name, int version, String domain, Object payload,
                             String status, String releaseId) {
        try {
            String json = jsonMapper.writeValueAsString(payload);
            String checksum = ChecksumUtil.sha256(json);


            jdbc.update("""
                    INSERT INTO metadata_object_store(object_type, object_name, version, domain, payload_json,
                    checksum, status, release_id)
                    VALUES (?, ?, ?, ?, CAST(? AS jsonb), ?, ?, ?)
                    ON CONFLICT (object_type, object_name, version)
                    DO UPDATE SET payload_json=EXCLUDED.payload_json,
                    checksum=EXCLUDED.checksum,
                    status=EXCLUDED.status,
                    release_id=EXCLUDED.release_id
                    """, type, name, version, domain, json, checksum, status, releaseId);


        } catch (Exception e) {
            throw new RuntimeException("Failed upserting object: " + type + ":" + name, e);
        }
    }


    public void deactivateAllActiveForName(String type, String name) {
        jdbc.update("""
                UPDATE metadata_object_store
                SET status='DEPRECATED'
                WHERE object_type=? AND object_name=? AND status='ACTIVE'
                """, type, name);
    }


    public boolean existsActiveOrDraft(String type, String name) {
        Integer count = jdbc.queryForObject("""
                SELECT count(*)
                FROM metadata_object_store
                WHERE object_type=? AND object_name=?
                """, Integer.class, type, name);
        return count > 0;
    }

    public String getDomainDefaultConnectionRef(String domain) {
        return jdbc.query("""
        SELECT payload_json->>'connectionRef'
        FROM metadata_object_store
        WHERE object_type='DOMAIN_DEFAULTS'
          AND object_name=?
        ORDER BY version DESC
        LIMIT 1
        """, rs -> rs.next() ? rs.getString(1) : null, domain);
    }

    public boolean pipelineTemplateExists(String templateRef) {
        Integer count = jdbc.queryForObject("""
      SELECT count(*)
      FROM metadata_object_store
      WHERE object_type='PIPELINE_TEMPLATE'
        AND object_name=?
      """, Integer.class, templateRef);

        return count > 0;
    }

    public int getNextVersion(
            String objectType,
            String objectName) {

        Integer v = jdbc.queryForObject("""
        SELECT COALESCE(MAX(version), 0) + 1
        FROM metadata_object_store
        WHERE object_type = ?
          AND object_name = ?
    """,
                Integer.class,
                objectType,
                objectName
        );

        return v;
    }


}