package com.example.bankcards.security;

import com.example.bankcards.entity.Token;
import com.example.bankcards.repository.TokenRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CustomLogoutHandler implements LogoutHandler {
    private final TokenRepository tokenRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader(JwtFilter.HEADER_AUTH);
        if (authHeader == null || !authHeader.startsWith(JwtFilter.AUTH_BEARER_PREFIX)) {
            return;
        }
        String token = authHeader.substring(JwtFilter.AUTH_BEARER_PREFIX.length());
        Token tokenEntity = tokenRepository.findTokenByAccessToken(token).orElse(null);
        if (tokenEntity != null) {
            tokenEntity.setLoggedOut(true);
            tokenRepository.save(tokenEntity);
        }
    }
}
