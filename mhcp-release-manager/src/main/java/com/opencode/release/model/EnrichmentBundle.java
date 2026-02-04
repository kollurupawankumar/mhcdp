package com.opencode.release.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;
import java.util.Map;


public record EnrichmentBundle(
        @NotBlank String apiVersion,
        @NotBlank String kind,
        @NotNull @Valid Metadata metadata,
        @NotNull @Valid Spec spec
) {
    public record Metadata(@NotBlank String domain, @NotNull Integer version) {}


    public record Spec(
            @NotNull List<EnrichmentDef> enrichments
    ) {}


    public record EnrichmentDef(
            @NotBlank String name,
            @NotBlank String strategy,
            List<String> inputKeys,
            Map<String, Object> lookup,
            List<Map<String, Object>> outputFields
    ) {}
}