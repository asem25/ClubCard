package ru.semavin.ClubCard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClubMemberRegisterDTO {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String phone;
}
