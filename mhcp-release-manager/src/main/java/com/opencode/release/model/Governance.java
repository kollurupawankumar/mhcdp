package com.opencode.release.model;



import java.util.List;

public record Governance(

        List<String> pii_columns,
        Boolean encryption,
        Boolean lineage_capture

) {}

