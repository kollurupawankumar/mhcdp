package com.opencode.release.model;


public record Compute(

        String engine,          // spark
        String cluster_size,    // small | medium | large
        Boolean autoscaling

) {}
