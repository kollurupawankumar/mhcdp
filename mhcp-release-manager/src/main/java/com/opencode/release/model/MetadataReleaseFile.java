package com.opencode.release.model;

import java.util.List;

public record MetadataReleaseFile(

        String kind,        // ← MUST exist
        Metadata metadata,
        Spec spec

) {

    public record Metadata(
            String name
    ) {}

    public record Spec(
            String env,
            List<Bundle> bundles
    ) {}

    public record Bundle(
            String kind,
            String file
    ) {}
}
