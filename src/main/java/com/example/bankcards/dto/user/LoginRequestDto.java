package com.example.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @Size(min = 2, max = 255)
    @NotBlank
    private String username;
    @Size(min = 2, max = 255)
    @NotBlank
    private String password;
}
