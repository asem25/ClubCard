package ru.semavin.ClubCard.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.util.ClubMemberEmailAlreadyUsed;
import ru.semavin.ClubCard.util.ClubMemberNotFoundException;

import java.util.*;
@Service
public class AuthService {
    private final ClubMemberService clubMemberService;
    private final PasswordEncoder passwordEncoder;

    public AuthService(ClubMemberService clubMemberService, PasswordEncoder passwordEncoder) {
        this.clubMemberService = clubMemberService;
        this.passwordEncoder = passwordEncoder;
    }

    public ClubMember registerMember(ClubMemberRegisterDTO memberDTO) {
        try {
            ClubMember clubMember = clubMemberService.findByEmail(memberDTO.getEmail());
        }catch (ClubMemberNotFoundException e){
            throw new ClubMemberEmailAlreadyUsed("User with email already use");
        }




        return clubMemberService.save(ClubMember.builder()
                .email(memberDTO.getEmail())
                .password(passwordEncoder.encode(memberDTO.getPassword()))
                .birthday(memberDTO.getBirthday())
                .role("USER")
                .firstName(memberDTO.getFirstName())
                .lastName(memberDTO.getLastName())
                .privilege(List.of("basic"))
                .isLocked(false)
                .build());
    }
}
