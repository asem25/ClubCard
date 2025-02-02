package ru.semavin.ClubCard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.models.BlackListToken;

@Service
public class LogoutService {
    private final RefreshTokenService refreshTokenService;
    private final BlackListTokenService blackListTokenService;
    @Autowired
    public LogoutService(RefreshTokenService refreshTokenService, BlackListTokenService blackListTokenService) {
        this.refreshTokenService = refreshTokenService;
        this.blackListTokenService = blackListTokenService;
    }
    public void invalidateTokens(String accessToken, String refreshToken) {
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }


        blackListTokenService.save(BlackListToken.builder()
                        .token(accessToken)
                        .build());

        refreshTokenService.deleteByToken(refreshToken);
    }
    public boolean isTokenBlackListed(String token){
        return !blackListTokenService.tokenExists(token);
    }

}
