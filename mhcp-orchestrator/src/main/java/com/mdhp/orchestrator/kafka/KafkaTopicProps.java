package com.mdhp.orchestrator.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mdhp.kafka.topics")
public record KafkaTopicProps(
        String ingestionCommand,
        String transformationCommand,
        String enrichmentCommand,
        String stepStatus
) {}
