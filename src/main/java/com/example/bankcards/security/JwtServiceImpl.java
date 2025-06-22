package com.example.bankcards.security;

import com.example.bankcards.entity.Token;
import com.example.bankcards.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.impl.lang.Function;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl implements JwtService {
    @Value("${security.jwt.secret_key}")
    private String secretKey;
    @Value("${security.jwt.access_token_expiration}")
    private long accessTokenExpiration;
    @Value("${security.jwt.refresh_token_expiration}")
    private long refreshTokenExpiration;
    private final TokenRepository tokenRepository;

    @Override
    public String generateAccessToken(UserDetails userDetails) {
        log.debug("==> Generating access token for user: {}", userDetails.getUsername());
        String token = generateToken(userDetails, accessTokenExpiration);
        log.debug("<== Generated access token for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {
        log.debug("==> Generating refresh token for user: {}", userDetails.getUsername());
        String token = generateToken(userDetails, refreshTokenExpiration);
        log.debug("<== Generated refresh token for user: {}", userDetails.getUsername());
        return token;
    }

    private SecretKey getSecretKey() {
        log.debug("==> Getting secret key");
        byte[] keyBytes = Decoders.BASE64URL.decode(secretKey);
        SecretKey key = Keys.hmacShaKeyFor(keyBytes);
        log.debug("<== Secret key obtained");
        return key;
    }

    private String generateToken(UserDetails userDetails, long lifetimeToken) {
        Date start = new Date();
        Date expiration = new Date(start.getTime() + lifetimeToken);

        log.debug("==> Building token for user: {}, issuedAt: {}, expiresAt: {}",
                userDetails.getUsername(), start, expiration);

        String token = Jwts.builder()
                .subject(userDetails.getUsername())
                .issuedAt(start)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();

        log.debug("<== Successfully generated token for user: {}", userDetails.getUsername());
        return token;
    }

    @Override
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        log.debug("==> Extracting claim from token");
        Claims claims = extractAllClaims(token);
        T result = claimsTFunction.apply(claims);
        log.debug("<== Claim extracted from token");
        return result;
    }

    @Override
    public String extractUsername(String token) {
        log.debug("==> Extracting username from token");
        String username = extractClaim(token, Claims::getSubject);
        log.debug("<== Extracted username: {} from token", username);
        return username;
    }

    private Date extractExpiration(String token) {
        log.debug("==> Extracting expiration date from token");
        Date expiration = extractClaim(token, Claims::getExpiration);
        log.debug("<== Extracted expiration: {} from token", expiration);
        return expiration;
    }

    private Claims extractAllClaims(String token) {
        log.debug("==> Extracting all claims from token");
//TODO        Вызывает следующее исключение при неверном ключе:
//        IllegalArgumentException("PrivateKeys may not be used to verify digital signatures. PrivateKeys are used to sign, and PublicKeys are used to verify.")
        Claims claims = Jwts.parser()
                .verifyWith(getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        log.debug("<== Successfully extracted claims from token");
        return claims;
    }

    @Override
    public boolean isValid(String token, UserDetails userDetails) {
        log.debug("==> Validating access token for user: {}", userDetails.getUsername());

        boolean isLoggedOut = tokenRepository.findTokenByAccessToken(token)
                .map(Token::isLoggedOut)
                .orElse(true);

        boolean isValid = userDetails.getUsername().equals(extractUsername(token))
                && isAccessTokenExpired(token)
                && !isLoggedOut;

        log.debug("<== Access token validation result: {}", isValid);
        return isValid;
    }

    @Override
    public boolean isValidRefresh(String token, UserDetails userDetails) {
        log.debug("==> Validating refresh token for user: {}", userDetails.getUsername());

        boolean isValidRefreshToken = tokenRepository.findTokenByRefreshToken(token)
                .map(t -> !t.isLoggedOut())
                .orElse(false);

        boolean isValid = userDetails.getUsername().equals(extractUsername(token))
                && isAccessTokenExpired(token)
                && isValidRefreshToken;

        log.debug("<== Refresh token validation result: {}", isValid);
        return isValid;
    }

    private boolean isAccessTokenExpired(String token) {
        log.debug("==> Checking token expiration");
        boolean isExpired = extractExpiration(token).after(new Date());
        log.debug("<== Token expiration check: {}", isExpired ? "valid" : "expired");
        return isExpired;
    }
}
