package com.opencode.release.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


import java.util.List;
import java.util.Map;


public record DomainBundle(
        @NotBlank String apiVersion,
        @NotBlank String kind,
        @NotNull @Valid Metadata metadata,
        @NotNull @Valid Spec spec
) {
    public record Metadata(@NotBlank String domain, @NotNull Integer version) {}


    public record Spec(
            Defaults defaults,
            List<ConnectionProfileDef> connections,
            //List<SourceDef> sources,
            List<PipelineTemplateDef> pipelineTemplates,
            List<TransformationDef> transformations,
            List<DatasetDef> datasets
    ) {}

    public record Defaults(
        String connectionRef
     ) {}

    public record ConnectionProfileDef(
            String name,
            String type,
            Map<String, Object> secretRefs,
            String baseUrl,
            Map<String, Object> auth,
            Map<String, Object> properties
    ) {
    }


    public record SourceDef(
            @NotBlank String name,
            @NotBlank String type,
            Map<String, Object> connection,
            Map<String, Object> read
    ) {}


    public record PipelineTemplateDef(
            @NotBlank String name,
            @NotNull List<StepDef> steps
    ) {}


    public record StepDef(
            @NotBlank String name,
            @NotBlank String type,
            List<String> dependsOn
    ) {}


    public record TransformationDef(
            @NotBlank String name,
            Map<String, Object> input,
            Map<String, Object> output,
            List<Map<String, Object>> rules
    ) {}


    public record DatasetDef(
            @NotBlank String name,
            @NotNull Map<String, Object> source,
            @NotNull Map<String, Object> pipeline,
            Map<String, Object> transformation,
            Map<String, Object> enrichment,
            Map<String, Object> target,
            Map<String, Object> read,
            Map<String, Object> write
    ) {}
}
