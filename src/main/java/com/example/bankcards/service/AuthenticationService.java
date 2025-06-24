package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationResponseDto;
import com.example.bankcards.dto.user.LoginRequestDto;
import com.example.bankcards.dto.user.RegistrationRequestDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface AuthenticationService {
    void register(RegistrationRequestDto request);

    AuthenticationResponseDto authenticate(LoginRequestDto request);

    ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response);
}
