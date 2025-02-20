package ru.semavin.ClubCard.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.semavin.ClubCard.models.ClubMember;

import java.util.Optional;

public interface  ClubMemberRepository extends JpaRepository<ClubMember, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<ClubMember> findByEmail(String Email);
}
