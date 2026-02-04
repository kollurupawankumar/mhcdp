package com.opencode.release.config;

import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "mhcp.release")
public record ReleaseManagerProps(
        String defaultStatus,
        boolean strictValidation
) {}