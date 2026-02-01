package com.mdhp.orchestrator.kafka;

public interface PipelineEventPublisher {
    void publish(String topic, String key, String payload);
}

