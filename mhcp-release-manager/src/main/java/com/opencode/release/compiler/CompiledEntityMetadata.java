package com.opencode.release.compiler;

import java.util.Map;

public record CompiledEntityMetadata(
        String domainCode,
        String entityId,
        String entityName,
        Map<String, Object> sourceConfig,
        Map<String, Object> transformConfig,
        Map<String, Object> enrichConfig
) {}
