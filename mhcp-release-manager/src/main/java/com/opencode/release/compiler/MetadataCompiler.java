package com.opencode.release.compiler;

import com.opencode.release.model.ConnectionBundle;
import com.opencode.release.model.DomainBundle;
import com.opencode.release.model.EnrichmentBundle;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class MetadataCompiler {

    // -------------------------
    // Index building helpers
    // -------------------------

    public Map<String, DomainBundle.TransformationDef> indexTransformations(DomainBundle bundle) {
        if (bundle == null || bundle.spec() == null || bundle.spec().transformations() == null) {
            return Map.of();
        }
        return bundle.spec().transformations()
                .stream()
                .collect(Collectors.toMap(DomainBundle.TransformationDef::name, x -> x));
    }

    public Map<String, EnrichmentBundle.EnrichmentDef> indexEnrichments(EnrichmentBundle bundle) {
        if (bundle == null || bundle.spec() == null || bundle.spec().enrichments() == null) {
            return Map.of();
        }
        return bundle.spec().enrichments()
                .stream()
                .collect(Collectors.toMap(EnrichmentBundle.EnrichmentDef::name, x -> x));
    }

    // -------------------------
    // Main compile method
    // -------------------------

    public CompiledEntityMetadata compile(
            String domain,
            int version,
            DomainBundle.DatasetDef ds,
            ConnectionBundle connectionBundle,
            Map<String, DomainBundle.TransformationDef> transformationIndex,
            Map<String, EnrichmentBundle.EnrichmentDef> enrichmentIndex
    ) {
        if (ds == null) throw new IllegalArgumentException("DatasetDef is null");

        String entityId = ds.name();
        String entityName = simpleEntityName(entityId);

        // 1) SOURCE CONFIG
        Map<String, Object> sourceConfig = compileSourceConfig(domain, ds, connectionBundle);

        // 2) TRANSFORMATION CONFIG
        Map<String, Object> transformConfig =
                compileTransformationConfig(ds, transformationIndex);

        // 3) ENRICHMENT CONFIG
        Map<String, Object> enrichConfig =
                compileEnrichmentConfig(ds, enrichmentIndex);

        return new CompiledEntityMetadata(
                domain,
                entityId,
                entityName,
                sourceConfig,
                transformConfig,
                enrichConfig
        );
    }

    // -------------------------
    // Source compilation (NO refs)
    // -------------------------

    private Map<String, Object> compileSourceConfig(
            String domain,
            DomainBundle.DatasetDef ds,
            ConnectionBundle connectionBundle
    ) {
        Map<String, Object> out = new LinkedHashMap<>();

        Map<String, Object> dsSource = safeMap(ds.source());

        // Resolve connectionRef: dataset.source.connectionRef OR domain defaults
        String connectionRef = stringOrNull(dsSource.get("connectionRef"));

        if (connectionRef == null && connectionBundle != null &&
                connectionBundle.spec() != null &&
                connectionBundle.spec().defaults() != null) {
            connectionRef = connectionBundle.spec().defaults().connectionRef();
        }

        if (connectionRef == null) {
            throw new IllegalArgumentException(
                    "Missing connectionRef for dataset=" + ds.name() +
                            ". Provide dataset.source.connectionRef or domain defaults.connectionRef"
            );
        }

        ConnectionBundle.ConnectionProfileDef conn = findConnection(connectionBundle, connectionRef);

        // Embed full connection details (NO refs)
        out.put("sourceType", conn.type());
        if (conn.baseUrl() != null) out.put("baseUrl", conn.baseUrl());
        if (conn.secretRefs() != null) out.put("secretRefs", conn.secretRefs());
        if (conn.auth() != null) out.put("auth", conn.auth());
        if (conn.properties() != null) out.put("properties", conn.properties());

        // Embed dataset source specifics (entity/table/topic/path etc.)
        // These keys must match your YAML dataset.source keys
        copyIfPresent(dsSource, out, "entity");
        copyIfPresent(dsSource, out, "table");
        copyIfPresent(dsSource, out, "topic");
        copyIfPresent(dsSource, out, "path");
        copyIfPresent(dsSource, out, "extractionMode");
        copyIfPresent(dsSource, out, "checkpointKey");
        copyIfPresent(dsSource, out, "format"); // sometimes source declares format

        // Embed "read" section if present in dataset
        if (ds.read() != null) {
            out.put("read", ds.read());
        }

        // Important: remove connectionRef completely (do not store)
        // out does not contain it at all ✅

        return out;
    }

    private ConnectionBundle.ConnectionProfileDef findConnection(ConnectionBundle bundle, String connectionRef) {
        if (bundle == null || bundle.spec() == null || bundle.spec().connections() == null) {
            throw new IllegalArgumentException("ConnectionBundle not loaded, cannot resolve " + connectionRef);
        }
        return bundle.spec().connections()
                .stream()
                .filter(c -> connectionRef.equalsIgnoreCase(c.name()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Connection profile not found: " + connectionRef));
    }

    // -------------------------
    // Transformation compilation (NO refs)
    // -------------------------

    private Map<String, Object> compileTransformationConfig(
            DomainBundle.DatasetDef ds,
            Map<String, DomainBundle.TransformationDef> transformationIndex
    ) {
        if (ds.transformation() == null) return null;

        Object refObj = ds.transformation().get("ref");
        if (refObj == null) return null;

        String trRef = String.valueOf(refObj);

        DomainBundle.TransformationDef tr = transformationIndex.get(trRef);
        if (tr == null) {
            throw new IllegalArgumentException(
                    "Transformation not found: " + trRef + " (dataset=" + ds.name() + ")"
            );
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("engine", "SPARK"); // keep constant or read from YAML if you support engine
        out.put("input", tr.input());
        out.put("output", tr.output());
        out.put("rules", tr.rules());

        // Dataset-level overrides (optional)
        Object overrides = ds.transformation().get("overrides");
        if (overrides != null) {
            out.put("overrides", overrides);
        }

        // NOTE: Do NOT store "ref" in output ✅
        return out;
    }

    // -------------------------
    // Enrichment compilation (resolve refs -> full objects)
    // -------------------------

    private Map<String, Object> compileEnrichmentConfig(
            DomainBundle.DatasetDef ds,
            Map<String, EnrichmentBundle.EnrichmentDef> enrichmentIndex
    ) {
        if (ds.enrichment() == null) return null;

        Object rawRefs = ds.enrichment().get("refs");
        if (rawRefs == null) return null;

        List<String> refs = toStringList(rawRefs);

        if (refs.isEmpty()) return null;

        List<Map<String, Object>> resolved = new ArrayList<>();

        for (String ref : refs) {
            EnrichmentBundle.EnrichmentDef enr = enrichmentIndex.get(ref);
            if (enr == null) {
                throw new IllegalArgumentException(
                        "Enrichment not found: " + ref + " (dataset=" + ds.name() + ")"
                );
            }

            // Fully resolved enrichment object (NO refs)
            Map<String, Object> e = new LinkedHashMap<>();
            e.put("name", enr.name());
            e.put("strategy", enr.strategy());
            e.put("inputKeys", enr.inputKeys());
            e.put("lookup", enr.lookup());
            e.put("outputFields", enr.outputFields());

            resolved.add(e);
        }

        Map<String, Object> out = new LinkedHashMap<>();
        out.put("mode", "RESOLVED");
        out.put("lookups", resolved);

        // NOTE: Do NOT store refs in output ✅
        return out;
    }

    // -------------------------
    // Utilities
    // -------------------------

    private Map<String, Object> safeMap(Map<String, Object> m) {
        return m == null ? Map.of() : m;
    }

    private void copyIfPresent(Map<String, Object> src, Map<String, Object> dest, String key) {
        if (src.containsKey(key) && src.get(key) != null) {
            dest.put(key, src.get(key));
        }
    }

    private String stringOrNull(Object o) {
        if (o == null) return null;
        String s = String.valueOf(o).trim();
        return s.isEmpty() ? null : s;
    }

    private List<String> toStringList(Object raw) {
        if (raw instanceof List<?> list) {
            return list.stream().map(String::valueOf).toList();
        }
        if (raw instanceof String s && !s.isBlank()) {
            return List.of(s);
        }
        return List.of();
    }

    private String simpleEntityName(String datasetName) {
        int idx = datasetName.lastIndexOf('.');
        return idx > 0 ? datasetName.substring(idx + 1) : datasetName;
    }
}
