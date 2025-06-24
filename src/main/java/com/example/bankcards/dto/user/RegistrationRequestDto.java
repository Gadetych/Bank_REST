package com.example.bankcards.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegistrationRequestDto {
    @Email
    @NotBlank
    @Size(min = 6, max = 255)
    private String email;
    @Size(min = 2, max = 255)
    @NotBlank
    private String username;
    @Size(min = 6, max = 255)
    private String password;
}
