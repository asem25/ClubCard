package ru.semavin.ClubCard.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.dto.AuthResponse;

@Service
@Slf4j
public class KafkaEventProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public KafkaEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendRegisterEvent(String key, Object message) {
        kafkaTemplate.send("user.register.response", key, message);
        log.info("Send register message to Kafka: " + message);
    }
    public void sendLoginEvent(String key, Object message) {
        kafkaTemplate.send("user.login.response", key, message);
        log.info("Send login message to Kafka: " + message);
    }

    public void sendRefreshEvent(String key, Object message) {
        kafkaTemplate.send("user.token.response", key, message);
        log.info("Send refresh message to Kafka: " + message);
    }
    public void sendLogoutEvent(String key, Object message){
        kafkaTemplate.send("user.logout.response", key, message);
        log.info("Send logout message to Kafka: " + message);
    }
    public void sendValidateEvent(String key, Object message){
        kafkaTemplate.send("user.validate.response", key, message);
        log.info("Send validate message to Kafka: " + message);
    }
}
