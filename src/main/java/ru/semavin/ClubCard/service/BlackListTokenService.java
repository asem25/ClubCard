package ru.semavin.ClubCard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.models.BlackListToken;
import ru.semavin.ClubCard.repositories.BlackListTokenRepository;

@Service
public class BlackListTokenService {
    private final BlackListTokenRepository blackListTokenRepository;
    @Autowired
    public BlackListTokenService(BlackListTokenRepository blackListTokenRepository) {
        this.blackListTokenRepository = blackListTokenRepository;
    }
    public boolean tokenExists(String token){
        return blackListTokenRepository.existsByTokenIgnoreCase(token);
    }
    public void save(BlackListToken blackListToken){
        blackListTokenRepository.save(blackListToken);
    }
}
