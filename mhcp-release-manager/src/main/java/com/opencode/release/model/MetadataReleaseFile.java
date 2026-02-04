package com.opencode.release.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;
import java.util.Map;


public record MetadataReleaseFile(
        @NotBlank String apiVersion,
        @NotBlank String kind,
        @NotNull @Valid Metadata metadata,
        @NotNull @Valid Spec spec
) {
    public record Metadata(@NotBlank String name) {}


    public record Spec(
            @NotBlank String env,
            @NotNull List<BundleRef> bundles,
            @NotNull Activate activate
    ) {}


    public record BundleRef(
            @NotBlank String kind,
            @NotBlank String file
    ) {}


    public record Activate(
            List<String> datasets,
            List<String> enrichments,
            List<String> sources,
            List<String> transformations,
            List<String> pipelineTemplates
    ) {}
}
