package com.opencode.release.model;


public record WatermarkStorage(

        String type,    // metadata_db | file
        String table

) {}

