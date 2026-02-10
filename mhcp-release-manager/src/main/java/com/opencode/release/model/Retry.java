package com.opencode.release.model;



public record Retry(

        Integer max_attempts,
        Integer backoff_seconds

) {}

