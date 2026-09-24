package com.nutricare.nutricare_api.core.domain.entity.user;

import java.util.UUID;

public class RefreshTokenReuseDetectedException extends RuntimeException {
    private final UUID jti;

    public RefreshTokenReuseDetectedException(UUID jti) {
        super("Refresh token reuse detected for jti " + jti);
        this.jti = jti;
    }

    public UUID getJti() {
        return jti;
    }
}
