package com.opencode.release.model;

public record Extraction(

        String strategy,   // table | query | snapshot

        String query

) {}

