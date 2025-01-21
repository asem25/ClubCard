package ru.semavin.ClubCard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.dto.AuthResponse;
import ru.semavin.ClubCard.dto.ClubMemberLoginDTO;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.models.RefreshToken;
import ru.semavin.ClubCard.security.jwt.JwtTokenProvider;
import ru.semavin.ClubCard.util.AuthErrorException;
import ru.semavin.ClubCard.util.ClubMemberEmailAlreadyUsed;


import java.util.*;
@Service
@Slf4j
public class AuthService {
    private final ClubMemberService clubMemberService;
    private final JwtTokenProvider provider;
    private final RefreshTokenService refreshTokenService;

    public AuthService(ClubMemberService clubMemberService, JwtTokenProvider provider, RefreshTokenService refreshTokenService) {
        this.clubMemberService = clubMemberService;
        this.provider = provider;
        this.refreshTokenService = refreshTokenService;
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
                .privilege(List.of("basic"))
                .isLocked(false)
                .build());
    }
    public AuthResponse loginMember(ClubMemberLoginDTO memberLoginDTO){
        ClubMember clubMember = clubMemberService.findByEmail(memberLoginDTO.getEmail());
        if (!provider.validatePassword(memberLoginDTO.getPassword(), clubMember.getPassword()))
            throw new AuthErrorException("Invalid email or password");

        refreshTokenService.deleteByClubMember(clubMember);

        String accessToken = provider.generateToken(memberLoginDTO.getEmail(), "ROLE_USER");
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(clubMember);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();

    }
}
