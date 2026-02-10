package com.opencode.release.model;


import java.util.List;

public record Target(

        String type,          // delta | iceberg | jdbc
        String catalog,
        String database,
        String table,
        String path,

        String write_mode,   // append | merge | overwrite
        List<String> merge_keys,
        List<String> partition_columns

) {}
