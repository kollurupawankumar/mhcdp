package com.opencode.release.model;


import java.util.List;
import java.util.Map;

public record Transformation(

        List<String> select_columns,

        Map<String, String> derived_columns,

        List<DataQualityRule> data_quality_rules

) {}

