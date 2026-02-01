package com.mdhp.orchestrator.kafka;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaPipelineEventPublisher implements PipelineEventPublisher {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaPipelineEventPublisher(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(String topic, String key, String payload) {
        kafkaTemplate.send(topic, key, payload);
    }
}
