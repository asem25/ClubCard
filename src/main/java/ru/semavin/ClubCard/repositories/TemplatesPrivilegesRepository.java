package ru.semavin.ClubCard.repositories;


import org.springframework.data.jpa.repository.JpaRepository;
import ru.semavin.ClubCard.models.TemplatesPrivilege;

import java.util.Optional;

public interface TemplatesPrivilegesRepository extends JpaRepository<TemplatesPrivilege, Long> {
    Optional<TemplatesPrivilege> findByTemplate(String template);
}
