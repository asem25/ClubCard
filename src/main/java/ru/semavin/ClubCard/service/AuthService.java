package ru.semavin.ClubCard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.dto.AuthResponse;
import ru.semavin.ClubCard.dto.ClubMemberLoginDTO;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.models.RefreshToken;
import ru.semavin.ClubCard.producer.KafkaEventProducer;
import ru.semavin.ClubCard.security.jwt.JwtTokenProvider;
import ru.semavin.ClubCard.util.AuthErrorException;
import ru.semavin.ClubCard.util.ClubMemberEmailAlreadyUsed;


import java.time.Instant;
@Service
@Slf4j
public class AuthService {
    private final KafkaEventProducer kafkaEventProducer;
    private final ClubMemberService clubMemberService;
    private final JwtTokenProvider provider;
    private final RefreshTokenService refreshTokenService;
    private final TemplatePrivilegeService templatePrivilegeService;

    public AuthService(KafkaEventProducer kafkaEventProducer, ClubMemberService clubMemberService, JwtTokenProvider provider, RefreshTokenService refreshTokenService, TemplatePrivilegeService templatePrivilegeService) {
        this.kafkaEventProducer = kafkaEventProducer;
        this.clubMemberService = clubMemberService;
        this.provider = provider;
        this.refreshTokenService = refreshTokenService;
        this.templatePrivilegeService = templatePrivilegeService;
    }
    public void registerMember(ClubMemberRegisterDTO memberDTO) {
        if (clubMemberService.existByEmail(memberDTO.getEmail()))
            throw new ClubMemberEmailAlreadyUsed("User with email already exists");

        clubMemberService.save(ClubMember.builder()
                .email(memberDTO.getEmail())
                .password(provider.encodePassword(memberDTO.getPassword()))
                .birthday(memberDTO.getBirthday())
                .role("USER")
                .firstName(memberDTO.getFirstName())
                .lastName(memberDTO.getLastName())
                .phone(memberDTO.getPhone())
                .privilegeTemplate(templatePrivilegeService.findByTemplate("user"))
                .isLocked(false)
                .build());

        kafkaEventProducer.sendRegisterEvent(memberDTO.getEmail(),String.format(
                "{\"eventType\":\"USER_REGISTERED\",\"email\":\"%s\",\"timestamp\":\"%s\"}",
                memberDTO.getEmail(), Instant.now()));
    }
    public AuthResponse loginMember(ClubMemberLoginDTO memberLoginDTO){
        ClubMember clubMember = clubMemberService.findByEmail(memberLoginDTO.getEmail());
        if (!provider.validatePassword(memberLoginDTO.getPassword(), clubMember.getPassword()))
            throw new AuthErrorException("Invalid email or password");

        refreshTokenService.deleteByClubMember(clubMember);

        String accessToken = provider.generateToken(memberLoginDTO.getEmail(), "ROLE_USER");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(clubMember);

        AuthResponse authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

        kafkaEventProducer.sendLoginEvent(memberLoginDTO.getEmail(), authResponse);


        return authResponse;

    }
}
