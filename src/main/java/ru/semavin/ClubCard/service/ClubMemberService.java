package ru.semavin.ClubCard.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.semavin.ClubCard.models.ClubMember;
import ru.semavin.ClubCard.repositories.ClubMemberRepository;
import ru.semavin.ClubCard.util.ClubMemberNotFoundException;

@Service
public class ClubMemberService {
    private ClubMemberRepository clubMemberRepository;
    @Autowired
    public ClubMemberService(ClubMemberRepository clubMemberRepository) {
        this.clubMemberRepository = clubMemberRepository;
    }
    public ClubMember findByEmail(String email){
        return clubMemberRepository.findByEmail(email)
                .orElseThrow(() -> new ClubMemberNotFoundException("Member not found"));
    }
    @Transactional
    public ClubMember save(ClubMember clubMember){
        return clubMemberRepository.save(clubMember);
    }
    public boolean existByEmail(String email){
        return clubMemberRepository.existsByEmailIgnoreCase(email);
    }
}
