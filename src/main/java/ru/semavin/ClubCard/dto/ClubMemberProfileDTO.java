package ru.semavin.ClubCard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ClubMemberProfileDTO {
    @Email(message = "Некорректный формат email")
    private String email;
    @Size(min = 8, message = "Пароль должен содержать не менее 8 символов")
    private String password;
    @NotBlank(message = "First name cannot be empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be empty")
    private String lastName;
    @Past(message = "Birthday must be in the past")
    @JsonFormat(pattern = "dd.MM.yyyy")
    private LocalDate birthday;
    @Pattern(regexp = "\\+7\\d{10}", message = "Phone number must be in the format +7[number]")
    private String phone;
    @Size(max = 50, message = "Привилегия должна содержать не более 50 символов")
    private String privilege;
    private Boolean isLocked;
    @Size(max = 50, message = "Роль должна содержать не более 50 символов")
    private String role;
}
