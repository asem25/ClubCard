package ru.semavin.ClubCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semavin.ClubCard.models.BlackListToken;

public interface BlackListTokenRepository extends JpaRepository<BlackListToken, Long> {
    boolean existsByTokenIgnoreCase(String token);
}
