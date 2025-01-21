package ru.semavin.ClubCard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
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
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "First name cannot be empty")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    private String lastName;

    @NotNull(message = "Birthday cannot be null")
    @Past(message = "Birthday must be in the past")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthday;

    @NotBlank(message = "Phone number cannot be empty")
    @Pattern(regexp = "\\+7\\d{10}", message = "Phone number must be in the format +7[number]")
    private String phone;
}
