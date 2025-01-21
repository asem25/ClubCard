package ru.semavin.ClubCard.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.semavin.ClubCard.dto.AuthResponse;
import ru.semavin.ClubCard.dto.ClubMemberLoginDTO;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.service.AuthService;
@RestController
@Slf4j
@RequestMapping("api/auth")
public class AuthController{
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody ClubMemberRegisterDTO dto){

        log.info(String.format("""
                        Map to api/auth/register
                        email: "%s"
                        phone: "%s"
                        birthday: "%s"
                        """, dto.getEmail(), dto.getPhone(), dto.getBirthday()));
        authService.registerMember(dto);
        return ResponseEntity.ok("User " + dto.getPhone() + " register");
    }
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody ClubMemberLoginDTO memberLoginDTO){
        log.info(String.format("""
                        Map to api/auth/login
                        email: "%s"
                        """, memberLoginDTO.getEmail()), memberLoginDTO.getEmail());
        return ResponseEntity.ok(authService.loginMember(memberLoginDTO));
    }
}
