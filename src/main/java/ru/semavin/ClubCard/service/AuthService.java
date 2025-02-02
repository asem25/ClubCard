package ru.semavin.ClubCard.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.dto.*;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.models.RefreshToken;
import ru.semavin.ClubCard.security.jwt.JwtTokenProvider;
import ru.semavin.ClubCard.util.AuthErrorException;
import ru.semavin.ClubCard.util.ClubMemberEmailAlreadyUsed;



@Service
@Slf4j
public class AuthService {
    private final ClubMemberService clubMemberService;
    private final JwtTokenProvider provider;
    private final RefreshTokenService refreshTokenService;
    private final TemplatePrivilegeService templatePrivilegeService;

    public AuthService(ClubMemberService clubMemberService, JwtTokenProvider provider, RefreshTokenService refreshTokenService, TemplatePrivilegeService templatePrivilegeService) {

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
    public AuthResponse refreshAccessToken(RefreshRequest refreshRequest){
        RefreshToken refreshToken = refreshTokenService.findByToken(refreshRequest.getRefreshToken());
        if (refreshTokenService.isRefreshTokenExpired(refreshToken)){
            refreshTokenService.deleteRefreshToken(refreshToken);
            throw new AuthErrorException("RefreshToken is expired");
        }
        return AuthResponse.builder()
                .refreshToken(refreshToken.getToken())
                .accessToken(provider.generateToken(refreshToken.getClubMember().getEmail(), "ROLE_USER"))
                .build();
    }
}
