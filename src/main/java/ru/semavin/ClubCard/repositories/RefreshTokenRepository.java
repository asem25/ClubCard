package ru.semavin.ClubCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.models.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);
    void deleteByClubMember(ClubMember clubMember);
}
