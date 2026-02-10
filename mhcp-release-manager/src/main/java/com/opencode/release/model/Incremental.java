package com.opencode.release.model;


public record Incremental(

        String column,
        WatermarkStorage watermark_storage

) {}

