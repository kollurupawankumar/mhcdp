package com.opencode.release.model;


import java.util.List;
import java.util.Map;

public record ConnectionBundle(
        String apiVersion,
        String kind,
        Metadata metadata,
        Spec spec
) {
    public record Metadata(String domain, Integer version) {}

    public record Spec(
            Defaults defaults,
            List<ConnectionProfileDef> connections
    ) {}

    public record Defaults(String connectionRef) {}

    public record ConnectionProfileDef(
            String name,
            String type,
            Map<String, Object> secretRefs,
            String baseUrl,
            Map<String, Object> auth,
            Map<String, Object> properties
    ) {}
}

