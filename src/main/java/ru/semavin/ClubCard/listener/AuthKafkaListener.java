package ru.semavin.ClubCard.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.semavin.ClubCard.dto.AuthResponse;
import ru.semavin.ClubCard.dto.ClubMemberLoginDTO;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.dto.KafkaResponse;
import ru.semavin.ClubCard.producer.KafkaEventProducer;
import ru.semavin.ClubCard.service.AuthService;
import ru.semavin.ClubCard.util.AuthErrorException;
import ru.semavin.ClubCard.util.ClubMemberEmailAlreadyUsed;
import ru.semavin.ClubCard.util.ValidationUtils;

import java.time.LocalDate;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class AuthKafkaListener {

    private final KafkaEventProducer kafkaEventProducer;
    private final ValidationUtils validationUtils;
    private final AuthService authService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthKafkaListener(KafkaEventProducer kafkaEventProducer, ValidationUtils validationUtils, AuthService authService, ObjectMapper objectMapper) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.validationUtils = validationUtils;
        this.authService = authService;
        this.objectMapper = objectMapper;
    }
    @KafkaListener(topics = "user.register.request")
    public void handleRegistration(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().equals("null")) {
            log.error("Received null value for topic {}", record.topic());
            return;
        }

        String key = record.key();
        try {
            ClubMemberRegisterDTO registerDTO = objectMapper.readValue(record.value(), ClubMemberRegisterDTO.class);
            try {
                validationUtils.validate(registerDTO);
            }catch (ConstraintViolationException e){
                sendRegisterResponse(key, "Validate error", e.getConstraintViolations()
                        .stream()
                        .map(constraintViolation ->
                                String.format("""
                                                                
                                                                Property: %s
                                                                Message: %s
                                                                """,
                                        constraintViolation.getPropertyPath(), constraintViolation.getMessage()))
                        .collect(Collectors.joining("")));
                return;
            }
            authService.registerMember(registerDTO);
            sendRegisterResponse(key, "SUCCESS", String.format("User %s register successful", registerDTO.getEmail()));
        } catch (ClubMemberEmailAlreadyUsed e) {
            sendRegisterResponse(key, "BAD REQUEST", e.getMessage());
        } catch (Exception e) {
            sendRegisterResponse(key, "ERROR", "Repeat request later");
        }
    }

    @KafkaListener(topics = "user.login.request")
    public void handleLogin(ConsumerRecord<String, String> record) {
        if (record.value() == null || record.value().equals("null")) {
            log.error("Received null value for topic {}", record.topic());
            return;
        }

        String key = record.key();
        try {
            ClubMemberLoginDTO loginDTO = objectMapper.readValue(record.value(), ClubMemberLoginDTO.class);
            try {
                validationUtils.validate(loginDTO);
            }catch (ConstraintViolationException e){
                sendBadLoginResponse(key, "Validate error", e.getConstraintViolations()
                        .stream()
                        .map(constraintViolation ->
                                String.format("""
                                                                
                                                                Property: %s
                                                                Message: %s
                                                                """,
                                        constraintViolation.getPropertyPath(), constraintViolation.getMessage()))
                        .collect(Collectors.joining("")));
                return;
            }

            AuthResponse response = authService.loginMember(loginDTO);

            kafkaEventProducer.sendLoginEvent(key, response);
        } catch (AuthErrorException e) {
            sendBadLoginResponse(key, "BAD REQUEST", e.getMessage());
        } catch (Exception e) {
            sendBadLoginResponse(key, "ERROR", "Repeat attempt later");
        }
    }
    private void sendRegisterResponse(String key, String eventType, String text) {
        kafkaEventProducer.sendRegisterEvent(key, KafkaResponse.builder()
                .eventType(eventType)
                .text(text)
                .timestamp(LocalDate.now().toString())
                .build());
    }
    private void sendBadLoginResponse(String key, String eventType, String text) {
        kafkaEventProducer.sendRegisterEvent(key, KafkaResponse.builder()
                .eventType(eventType)
                .text(text)
                .timestamp(LocalDate.now().toString())
                .build());
    }
}
