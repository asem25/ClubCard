package ru.semavin.ClubCard.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.semavin.ClubCard.dto.*;
import ru.semavin.ClubCard.producer.KafkaEventProducer;
import ru.semavin.ClubCard.service.AuthService;
import ru.semavin.ClubCard.service.LogoutService;
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
    private final LogoutService logoutService;
    private final ObjectMapper objectMapper;

    @Autowired
    public AuthKafkaListener(KafkaEventProducer kafkaEventProducer, ValidationUtils validationUtils, AuthService authService, LogoutService logoutService, ObjectMapper objectMapper) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.validationUtils = validationUtils;
        this.authService = authService;
        this.logoutService = logoutService;
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
            sendBadLoginResponse(key, "ERROR", e.getMessage());
        }
    }
    @KafkaListener(topics = "user.token.request")
    public void handleRefresh(ConsumerRecord<String, String> record)
    {
        if (record.value() == null || record.value().equals("null")) {
            log.error("Received null value for topic {}", record.topic());
            return;
        }
        String key = record.key();
        try {
            RefreshRequest refreshRequest = objectMapper.readValue(record.value(), RefreshRequest.class);
            AuthResponse authResponse = authService.refreshAccessToken(refreshRequest);

            kafkaEventProducer.sendRefreshEvent(key, authResponse);
        } catch (AuthErrorException e) {
            sendBadRefreshTokenResponse(key, "BAD TOKEN", e.getMessage());
        } catch (Exception e) {
            sendBadRefreshTokenResponse(key, "ERROR", e.getMessage());
        }
    }
    @KafkaListener(topics = "user.logout.request")
    public void handleLogout(ConsumerRecord<String, String> record){

        if (record.value() == null || record.value().equals("null")) {
            log.error("Received null value for topic {}", record.topic());
            return;
        }
        String key = record.key();
        try {
            AuthResponse authResponse = objectMapper.readValue(record.value(), AuthResponse.class);

            logoutService.invalidateTokens(authResponse.getAccessToken(), authResponse.getRefreshToken());

            kafkaEventProducer.sendLogoutEvent(key, KafkaResponse.builder()
                    .eventType("SUCCESS")
                    .text("Logout successful")
                    .timestamp(LocalDate.now().toString())
                    .build());
        }catch (Exception e){
            sendBadLogoutResponse(key, "ERROR", e.getMessage());
        }
    }
    @KafkaListener(topics = "user.validate.request")
    public void handleValidate(ConsumerRecord<String, String> record){

        if (record.value() == null || record.value().equals("null")) {
            log.error("Received null value for topic {}", record.topic());
            return;
        }
        String key = record.key();
        try {
            TokenResponse tokenResponse = objectMapper.readValue(record.value(), TokenResponse.class);

            boolean isValid = logoutService.isTokenBlackListed(tokenResponse.getJwtToken());

            kafkaEventProducer.sendValidateEvent(key, KafkaResponse.builder()
                    .eventType(isValid ? "VALID" : "NO VALID")
                    .text("Validation successful")
                    .timestamp(LocalDate.now().toString())
                    .build());
        }catch (Exception e){
            sendBadValidateResponse(key, "ERROR", e.getMessage());
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
    private void sendBadRefreshTokenResponse(String key, String eventType, String text){
        kafkaEventProducer.sendRefreshEvent(key, KafkaResponse.builder()
                .eventType(eventType)
                .text(text)
                .timestamp(LocalDate.now().toString())
                .build());
    }
    private void sendBadLogoutResponse(String key, String eventType, String text){
        kafkaEventProducer.sendLogoutEvent(key, KafkaResponse.builder()
                .eventType(eventType)
                .text(text)
                .timestamp(LocalDate.now().toString())
                .build());
    }
    private void sendBadValidateResponse(String key, String eventType, String text){
        kafkaEventProducer.sendValidateEvent(key, KafkaResponse.builder()
                .eventType(eventType)
                .text(text)
                .timestamp(LocalDate.now().toString())
                .build());
    }
}
