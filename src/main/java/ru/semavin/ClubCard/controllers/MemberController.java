package ru.semavin.ClubCard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.semavin.ClubCard.dto.ClubMemberProfileDTO;
import ru.semavin.ClubCard.service.ClubMemberService;

@RestController
@RequestMapping("api/member")
public class MemberController {
    private final ClubMemberService clubMemberService;
    @Autowired
    public MemberController(ClubMemberService clubMemberService) {
        this.clubMemberService = clubMemberService;
    }
    @PatchMapping("/role")
    public ResponseEntity<ClubMemberProfileDTO> changeRole(@RequestBody ClubMemberProfileDTO clubMemberProfileDTO){
        ClubMemberProfileDTO clubMember = clubMemberService.changeRole(clubMemberProfileDTO);
        return ResponseEntity.ok(clubMember);
    }
    @PatchMapping("/profile")
    public ResponseEntity<ClubMemberProfileDTO> changeProfile(@RequestBody ClubMemberProfileDTO clubMemberProfileDTO){
        ClubMemberProfileDTO clubMember = clubMemberService.updateClubMember(clubMemberProfileDTO);
        return  ResponseEntity.ok(clubMember);
    }
    @GetMapping("/profile")
    public ResponseEntity<ClubMemberProfileDTO> getProfile(@AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.ok(clubMemberService.getProfile(userDetails.getUsername()));
    }
}
