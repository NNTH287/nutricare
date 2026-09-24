package com.nutricare.nutricare_api.infrastructure.adapter.out.security;

import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.VerifiedRefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtTokenProvider implements TokenIssuer {
    private static final String CLAIM_EMAIL = "email";
    private static final String CLAIM_ROLE = "role";
    private static final String CLAIM_TYPE = "type";

    private final SecretKey key;
    private final long accessTokenExpirationMillis;
    private final long refreshTokenExpirationDays;

    public JwtTokenProvider(JwtProperties properties) {
        this.key = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpirationMillis = properties.expirationMinutes() * 60_000L;
        this.refreshTokenExpirationDays = properties.refreshTokenExpirationDays();
    }

    @Override
    public String issueAccessToken(Integer userId, String email, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(userId.toString())
                .claim(CLAIM_EMAIL, email)
                .claim(CLAIM_ROLE, role.name())
                .claim(CLAIM_TYPE, TokenType.ACCESS.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusMillis(accessTokenExpirationMillis)))
                .signWith(key)
                .compact();
    }

    @Override
    public IssuedRefreshToken issueRefreshToken(Integer userId, UUID jti) {
        LocalDateTime issuedAt = LocalDateTime.now();
        LocalDateTime expiresAt = issuedAt.plusDays(refreshTokenExpirationDays);

        String token = Jwts.builder()
                .id(jti.toString())
                .subject(userId.toString())
                .claim(CLAIM_TYPE, TokenType.REFRESH.name())
                .issuedAt(Date.from(toInstant(issuedAt)))
                .expiration(Date.from(toInstant(expiresAt)))
                .signWith(key)
                .compact();

        return new IssuedRefreshToken(token, expiresAt);
    }

    @Override
    public Optional<VerifiedRefreshToken> verifyRefreshToken(String token) {
        return validateAndParse(token, TokenType.REFRESH)
                .filter(claims -> claims.jti() != null)
                .map(claims -> new VerifiedRefreshToken(claims.userId(), claims.jti()));
    }

    public Optional<TokenClaims> validateAndParse(String token, TokenType expectedType) {
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            TokenType type = TokenType.valueOf(claims.get(CLAIM_TYPE, String.class));
            if (type != expectedType) {
                return Optional.empty();
            }
            Integer userId = Integer.valueOf(claims.getSubject());
            String email = claims.get(CLAIM_EMAIL, String.class);
            Role role = email != null ? Role.valueOf(claims.get(CLAIM_ROLE, String.class)) : null;
            UUID jti = claims.getId() != null ? UUID.fromString(claims.getId()) : null;
            return Optional.of(new TokenClaims(userId, email, role, type, jti));
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }

    private static Instant toInstant(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant();
    }
}
