package ru.semavin.ClubCard.service;

import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.models.TemplatesPrivilege;
import ru.semavin.ClubCard.repositories.TemplatesPrivilegesRepository;

@Service
public class TemplatePrivilegeService {
    private final TemplatesPrivilegesRepository templatesPrivilegesRepository;

    public TemplatePrivilegeService(TemplatesPrivilegesRepository templatesPrivilegesRepository) {
        this.templatesPrivilegesRepository = templatesPrivilegesRepository;
    }
    public TemplatesPrivilege findByTemplate(String template){
        return templatesPrivilegesRepository.findByTemplate(template).get();
    }
}
