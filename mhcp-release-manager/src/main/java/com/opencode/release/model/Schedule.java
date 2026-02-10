package com.opencode.release.model;


public record Schedule(

        String frequency,   // hourly | daily
        String timezone

) {}
