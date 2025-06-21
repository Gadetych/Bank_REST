package com.example.bankcards.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.lang.Function;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {
    String generateAccessToken(UserDetails userDetails);

    String generateRefreshToken(UserDetails userDetails);

    <T> T extractClaim(String token, Function<Claims, T> claimsTFunction);

    String extractUserName(String token);

    boolean isValid(String token, UserDetails userDetails);
    boolean isValidRefresh(String token, UserDetails userDetails);
}
