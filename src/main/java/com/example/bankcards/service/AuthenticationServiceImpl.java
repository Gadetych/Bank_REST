package com.example.bankcards.service;

import com.example.bankcards.dto.AuthenticationResponseDto;
import com.example.bankcards.dto.user.LoginRequestDto;
import com.example.bankcards.dto.user.RegistrationRequestDto;
import com.example.bankcards.dto.user.UserResponse;
import com.example.bankcards.entity.Token;
import com.example.bankcards.entity.User;
import com.example.bankcards.repository.TokenRepository;
import com.example.bankcards.repository.UserRepository;
import com.example.bankcards.security.JwtFilter;
import com.example.bankcards.security.JwtService;
import com.example.bankcards.util.mapper.Mapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;

    private final UserService userService;
    private final TokenRepository tokenRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public UserResponse register(RegistrationRequestDto request) {
        if (userService.existsByUsername(request.getUsername())) {
            throw new UsernameNotFoundException("Имя пользователя уже занято");
        }
        if (userService.existsByEmail(request.getEmail())) {
            throw new UsernameNotFoundException("Email уже занят");
        }
        User user = Mapper.registrationRequestDtoToUser(request, passwordEncoder);
        user = userRepository.save(user);
        return Mapper.userToUserResponse(user);
    }

    @Override
    public AuthenticationResponseDto authenticate(LoginRequestDto request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String accessToken = jwtService.generateAccessToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);
        revokeAllToken(user);
        saveUserToken(accessToken, refreshToken, user);
        return new AuthenticationResponseDto(accessToken, refreshToken);
    }

    @Override
    public ResponseEntity<AuthenticationResponseDto> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorizationHeader == null || !authorizationHeader.startsWith(JwtFilter.AUTH_BEARER_PREFIX)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authorizationHeader.substring(JwtFilter.AUTH_BEARER_PREFIX.length());
        String username = jwtService.extractUsername(token);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user found"));

        if (jwtService.isValidRefresh(token, user)) {
            String accessToken = jwtService.generateAccessToken(user);
            String refreshToken = jwtService.generateRefreshToken(user);
            revokeAllToken(user);
            saveUserToken(accessToken, refreshToken, user);
            return new ResponseEntity<>(new AuthenticationResponseDto(accessToken, refreshToken), HttpStatus.OK);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    private void revokeAllToken(User user) {
        List<Token> list = tokenRepository.findAllTokenByUserIdAndLoggedOut(user.getId(), false);
        if (!list.isEmpty()) {
            list.forEach(token -> token.setLoggedOut(true));
        }
        tokenRepository.saveAll(list);
    }

    private void saveUserToken(String accessToken, String refreshToken, User user) {
        Token token = Token.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .user(user)
                .build();
        tokenRepository.save(token);
    }


}
