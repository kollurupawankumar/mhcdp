package com.opencode.release.model;


import java.util.Map;

public record Source(

        String type,                 // oracle | postgres | file | api | kafka

        // DB fields
        String schema,
        String table,
        String query,

        // File fields
        String path,
        String format,

        // API fields
        String endpoint,
        String method,

        // Stream fields
        String topic,
        String bootstrap_servers,

        // Common connection block
        Map<String, Object> connection

) {}

