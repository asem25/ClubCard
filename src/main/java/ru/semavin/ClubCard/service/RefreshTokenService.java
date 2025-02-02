package ru.semavin.ClubCard.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.models.RefreshToken;
import ru.semavin.ClubCard.repositories.RefreshTokenRepository;
import ru.semavin.ClubCard.util.RefreshNotFoundException;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
public class RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(ClubMember clubMember){
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plus(7, ChronoUnit.MINUTES))
                .clubMember(clubMember)
                .build();
        save(refreshToken);
        return refreshToken;
    }
    public void save(RefreshToken refreshToken){
        refreshTokenRepository.save(refreshToken);
    }

    public RefreshToken findByToken(String token){
        return refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshNotFoundException("RefreshToken not founded"));
    }

    public boolean isRefreshTokenExpired(RefreshToken refreshToken){
        return refreshToken.getExpiryDate().isBefore(Instant.now());
    }
    @Transactional
    public void deleteByClubMember(ClubMember clubMember){
        refreshTokenRepository.deleteByClubMember(clubMember);
    }
    @Transactional
    public void deleteRefreshToken(RefreshToken refreshToken){ refreshTokenRepository.delete(refreshToken);}
    @Transactional
    public void deleteByToken(String token){refreshTokenRepository.deleteByToken(token);}
}
