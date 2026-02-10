package com.opencode.release.model;


public record Classification(

        Boolean contains_pii,
        Boolean contains_phi

) {}
