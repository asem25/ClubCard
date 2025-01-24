package ru.semavin.ClubCard.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${spring.kafka.topic.registration}")
    private String topicRegName;
    @Value("${spring.kafka.topic.login}")
    private String topicLogName;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRegisterEvent(String key, Object message) {
        kafkaTemplate.send(topicRegName, key, message);
        log.info("Send register message to Kafka: " + message);
    }
    public void sendLoginEvent(String key, Object message) {
        kafkaTemplate.send(topicLogName, key, message);
        log.info("Send login message to Kafka: " + message);
    }
}
