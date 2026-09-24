package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.port.in.PurgeExpiredRefreshTokensUseCase;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;

import java.time.LocalDateTime;

public class PurgeExpiredRefreshTokensService implements PurgeExpiredRefreshTokensUseCase {
    private final RefreshTokenRepository refreshTokenRepository;

    public PurgeExpiredRefreshTokensService(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public void purgeExpiredTokens() {
        refreshTokenRepository.deleteExpiredBefore(LocalDateTime.now());
    }
}
