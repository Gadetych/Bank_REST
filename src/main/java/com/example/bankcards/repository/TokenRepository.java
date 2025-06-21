package com.example.bankcards.repository;

import com.example.bankcards.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findTokenByAccessToken(String accessToken);
    Optional<Token> findTokenByRefreshToken(String refreshToken);
    List<Token> findAllTokenByUserIdAndLoggedOut(long userId, boolean loggedOut);
}
