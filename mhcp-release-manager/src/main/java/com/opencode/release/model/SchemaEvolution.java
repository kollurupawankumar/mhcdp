package com.opencode.release.model;

public record SchemaEvolution(

        Boolean allow_new_columns,
        Boolean allow_type_changes

) {}
