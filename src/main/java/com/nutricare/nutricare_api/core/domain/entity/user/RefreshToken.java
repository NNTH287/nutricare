package com.nutricare.nutricare_api.core.domain.entity.user;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class RefreshToken {
    private final UUID jti;
    private final Integer userId;
    private final LocalDateTime issuedAt;
    private final LocalDateTime expiresAt;
    private LocalDateTime revokedAt;
    private UUID replacedByJti;

    public static RefreshToken issue(UUID jti, Integer userId, LocalDateTime issuedAt, LocalDateTime expiresAt) {
        return new RefreshToken(jti, userId, issuedAt, expiresAt, null, null);
    }

    public static RefreshToken reconstitute(UUID jti, Integer userId, LocalDateTime issuedAt, LocalDateTime expiresAt,
                                             LocalDateTime revokedAt, UUID replacedByJti) {
        return new RefreshToken(jti, userId, issuedAt, expiresAt, revokedAt, replacedByJti);
    }

    private RefreshToken(UUID jti, Integer userId, LocalDateTime issuedAt, LocalDateTime expiresAt,
                          LocalDateTime revokedAt, UUID replacedByJti) {
        if (jti == null) {
            throw new InvalidRefreshTokenException("jti is required");
        }
        if (userId == null) {
            throw new InvalidRefreshTokenException("userId is required");
        }
        if (issuedAt == null || expiresAt == null || !expiresAt.isAfter(issuedAt)) {
            throw new InvalidRefreshTokenException("expiresAt must be after issuedAt");
        }
        this.jti = jti;
        this.userId = userId;
        this.issuedAt = issuedAt;
        this.expiresAt = expiresAt;
        this.revokedAt = revokedAt;
        this.replacedByJti = replacedByJti;
    }

    public boolean isExpired(LocalDateTime now) {
        return !now.isBefore(expiresAt);
    }

    public boolean isRevoked() {
        return revokedAt != null;
    }

    public void verifyUsable(LocalDateTime now) {
        if (isRevoked()) {
            throw new RefreshTokenReuseDetectedException(jti);
        }
        if (isExpired(now)) {
            throw new InvalidRefreshTokenException("Refresh token has expired");
        }
    }

    public void revoke(LocalDateTime now, UUID replacedByJti) {
        if (isRevoked()) {
            return;
        }
        this.revokedAt = now;
        this.replacedByJti = replacedByJti;
    }

    public UUID getJti() {
        return jti;
    }

    public Integer getUserId() {
        return userId;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public LocalDateTime getRevokedAt() {
        return revokedAt;
    }

    public UUID getReplacedByJti() {
        return replacedByJti;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof RefreshToken that)) return false;
        return Objects.equals(jti, that.jti);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(jti);
    }
}
