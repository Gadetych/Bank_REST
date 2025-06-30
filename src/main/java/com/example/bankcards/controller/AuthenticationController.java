package com.example.bankcards.controller;

import com.example.bankcards.dto.AuthenticationResponseDto;
import com.example.bankcards.dto.user.LoginRequestDto;
import com.example.bankcards.dto.user.RegistrationRequestDto;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.service.AuthenticationService;
import com.example.bankcards.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/registration")
    public UserResponse register(@RequestBody RegistrationRequestDto registrationDto) {
        log.info("==> create user = {}", registrationDto);
        return authenticationService.register(registrationDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponseDto> authenticate(@RequestBody LoginRequestDto request) {
        log.info("==> authenticate request = {}", request);
        return ResponseEntity.ok(authenticationService.authenticate(request));
    }

    @PostMapping("/refresh_token")
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        return authenticationService.refreshToken(request, response);
    }
}
