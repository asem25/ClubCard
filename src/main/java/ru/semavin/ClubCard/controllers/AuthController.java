package ru.semavin.ClubCard.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.semavin.ClubCard.dto.ClubMemberRegisterDTO;
import ru.semavin.ClubCard.service.AuthService;
//TODO login + application.properties
@RestController
@RequestMapping("api/auth")
public class AuthController{
    private final AuthService authService;
    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(ClubMemberRegisterDTO dto){
        return ResponseEntity.ok(authService.registerMember(dto));
    }
}
