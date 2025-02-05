package ru.semavin.ClubCard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.ClubCard.dto.ClubMemberProfileDTO;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.repositories.ClubMemberRepository;
import ru.semavin.ClubCard.util.ClubMemberNotFoundException;
import ru.semavin.ClubCard.util.EmptyFieldException;

@Service
public class ClubMemberService {
    private ClubMemberRepository clubMemberRepository;
    private TemplatePrivilegeService templatePrivilegeService;
    private PasswordEncoder passwordEncoder;
    @Autowired
    public ClubMemberService(ClubMemberRepository clubMemberRepository, TemplatePrivilegeService templatePrivilegeService, PasswordEncoder passwordEncoder) {
        this.clubMemberRepository = clubMemberRepository;
        this.templatePrivilegeService = templatePrivilegeService;
        this.passwordEncoder = passwordEncoder;
    }
    public ClubMember findByEmail(String email){
        return clubMemberRepository.findByEmail(email)
                .orElseThrow(() -> new ClubMemberNotFoundException("Member not found"));
    }
    @Transactional
    public void save(ClubMember clubMember){
        clubMemberRepository.save(clubMember);
    }
    public boolean existByEmail(String email){
        return clubMemberRepository.existsByEmailIgnoreCase(email);
    }

    @Transactional
    public ClubMemberProfileDTO updateClubMember(ClubMemberProfileDTO dto) {
        if (dto.getEmail() == null){
            throw  new EmptyFieldException("Field email is empty. PATCH Point /profile");
        }
        ClubMember member = findByEmail(dto.getEmail());

        if (dto.getEmail() != null) {
            member.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null) {
            member.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        if (dto.getFirstName() != null) {
            member.setFirstName(dto.getFirstName());
        }
        if (dto.getLastName() != null) {
            member.setLastName(dto.getLastName());
        }
        if (dto.getBirthday() != null) {
            member.setBirthday(dto.getBirthday());
        }
        if (dto.getPhone() != null) {
            member.setPhone(dto.getPhone());
        }
        if (dto.getPrivilege() != null) {
            member.setPrivilegeTemplate(templatePrivilegeService.findByTemplate(dto.getRole()));
        }
        if (dto.getIsLocked() != null) {
            member.setLocked(dto.getIsLocked());
        }
        return convertToDto(clubMemberRepository.save(member));
    }
    @Transactional
    public ClubMemberProfileDTO changeRole(ClubMemberProfileDTO dto){
        if (dto.getEmail() == null){
            throw  new EmptyFieldException("Field email is empty. GET Point /profile");
        }
        ClubMember clubMember = findByEmail(dto.getEmail());
        if (dto.getRole() == null){
            throw new EmptyFieldException("Field role is empty. Patch point /role");
        }
        clubMember.setRole(dto.getRole());
        clubMember.setPrivilegeTemplate(templatePrivilegeService.findByTemplate(dto.getRole()));
        return convertToDto(clubMemberRepository.save(clubMember));
    }
    public ClubMemberProfileDTO getProfile(String email){
        if (email == null){
            throw  new EmptyFieldException("Field email is empty. GET Point /profile");
        }
        return convertToDto(findByEmail(email));
    }
    private ClubMemberProfileDTO convertToDto(ClubMember clubMember){
        return ClubMemberProfileDTO.builder()
                .phone(clubMember.getPhone())
                .role(clubMember.getRole())
                .birthday(clubMember.getBirthday())
                .isLocked(clubMember.isLocked())
                .lastName(clubMember.getLastName())
                .firstName(clubMember.getFirstName())
                .privilege(clubMember.getPrivilegeTemplate().getTemplate())
                .email(clubMember.getEmail())
                .build();
    }
}
