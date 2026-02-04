package com.opencode.release.model;


import java.util.List;

public record PipelineBundle(
        String apiVersion,
        String kind,
        Metadata metadata,
        Spec spec
) {
    public record Metadata(String domain, Integer version) {}

    public record Spec(List<PipelineTemplateDef> pipelineTemplates) {}

    public record PipelineTemplateDef(
            String name,
            List<StepDef> steps,
            List<RouteDef> routing,
            List<StepDef> extraSteps
    ) {}

    public record StepDef(
            String id,
            String type,
            List<String> dependsOn
    ) {}

    public record RouteDef(
            String from,
            String on,
            String next
    ) {}
}

