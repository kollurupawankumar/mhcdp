package com.opencode.release.service;


import com.opencode.release.model.DomainBundle;
import com.opencode.release.model.EnrichmentBundle;
import com.opencode.release.repo.MetadataRepo;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ValidationService {

    private final MetadataRepo repo;

    public ValidationService(MetadataRepo repo) {
        this.repo = repo;
    }

    // --- Source validation by type template ---
    public void validateSource(DomainBundle.SourceDef s) {
        if (s.type() == null) throw new IllegalArgumentException("source.type missing for " + s.name());

        // common checks
        if (s.connection() == null) throw new IllegalArgumentException("source.connection missing for " + s.name());
        if (s.read() == null) throw new IllegalArgumentException("source.read missing for " + s.name());

        // type-specific template
        switch (s.type().toUpperCase()) {
            case "FILE", "FILE_SFTP" -> validateFileSource(s);
            case "DB", "DB_JDBC" -> validateJdbcSource(s);
            case "API", "API_FHIR" -> validateApiSource(s);
            case "KAFKA" -> validateKafkaSource(s);
            default -> throw new IllegalArgumentException("Unsupported source.type=" + s.type() + " for " + s.name());
        }
    }

    private void validateFileSource(DomainBundle.SourceDef s) {
        Map<String, Object> read = s.read();
        require(read, "format");
        require(read, "options");
        // example semantic rule
        if ("csv".equalsIgnoreCase(String.valueOf(read.get("format")))) {
            Map<String, Object> options = (Map<String, Object>) read.get("options");
            require(options, "delimiter");
        }
    }

    private void validateJdbcSource(DomainBundle.SourceDef s) {
        Map<String, Object> conn = s.connection();
        require(conn, "secretRefs");
        Map<String, Object> secretRefs = (Map<String, Object>) conn.get("secretRefs");
        require(secretRefs, "url");
        require(secretRefs, "user");
        require(secretRefs, "pass");
    }

    private void validateApiSource(DomainBundle.SourceDef s) {
        Map<String, Object> conn = s.connection();
        require(conn, "baseUrl");
    }

    private void validateKafkaSource(DomainBundle.SourceDef s) {
        Map<String, Object> conn = s.connection();
        require(conn, "secretRefs");
        Map<String, Object> secretRefs = (Map<String, Object>) conn.get("secretRefs");
        require(secretRefs, "bootstrapServers");

        Map<String, Object> read = s.read();
        require(read, "format");
        require(read, "options");
        Map<String, Object> options = (Map<String, Object>) read.get("options");
        require(options, "subscribe");
    }

    public void validateDatasetRefs(String domain, DomainBundle.DatasetDef ds) {

        Object tplRef = ds.pipeline().get("templateRef");
        if (tplRef == null) {
            throw new IllegalArgumentException("dataset.pipeline.templateRef missing for " + ds.name());
        }

        Object connectionRef = null;
        if (ds.source() != null) {
            connectionRef = ds.source().get("connectionRef");
        }

        // ✅ If dataset doesn’t specify connectionRef, fetch domain default from DB
        String defaultConnRef = repo.getDomainDefaultConnectionRef(domain);

        if (connectionRef == null && defaultConnRef == null) {
            throw new IllegalArgumentException(
                    "Missing connectionRef for dataset " + ds.name() +
                            ". Provide dataset.source.connectionRef or ConnectionBundle defaults.connectionRef"
            );
        }
    }


    public void validateEnrichment(EnrichmentBundle.EnrichmentDef e) {
        if (e.lookup() == null) throw new IllegalArgumentException("enrichment.lookup missing for " + e.name());
    }

    private static void require(Map<String, Object> map, String key) {
        if (map == null || map.get(key) == null) {
            throw new IllegalArgumentException("missing required field: " + key);
        }
    }
}
