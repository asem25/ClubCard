package ru.semavin.ClubCard.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.service.ClubMemberService;

@Service
public class ClubMemberDetailsService implements UserDetailsService {
    private final ClubMemberService clubMemberService;

    public ClubMemberDetailsService(ClubMemberService clubMemberService) {
        this.clubMemberService = clubMemberService;
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
            ClubMember user = clubMemberService.findByEmail(email);
            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        }
    }

