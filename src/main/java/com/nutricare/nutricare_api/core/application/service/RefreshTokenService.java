package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.in.RefreshTokenUseCase;
import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.application.port.out.VerifiedRefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidRefreshTokenException;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshTokenReuseDetectedException;
import com.nutricare.nutricare_api.core.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.UUID;

public class RefreshTokenService implements RefreshTokenUseCase {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final TokenIssuer tokenIssuer;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
                                TokenIssuer tokenIssuer) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public AuthenticatedUserResult refresh(String presentedRefreshToken) {
        VerifiedRefreshToken verified = tokenIssuer.verifyRefreshToken(presentedRefreshToken)
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        RefreshToken storedToken = refreshTokenRepository.findByJti(verified.jti())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        try {
            storedToken.verifyUsable(LocalDateTime.now());
        } catch (RefreshTokenReuseDetectedException ex) {
            revokeAllActiveTokensFor(storedToken.getUserId());
            throw ex;
        }

        User user = userRepository.findById(storedToken.getUserId())
                .orElseThrow(() -> new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        UUID newJti = UUID.randomUUID();
        storedToken.revoke(LocalDateTime.now(), newJti);
        refreshTokenRepository.save(storedToken);

        String accessToken = tokenIssuer.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        IssuedRefreshToken issuedRefreshToken = tokenIssuer.issueRefreshToken(user.getId(), newJti);
        refreshTokenRepository.save(RefreshToken.issue(newJti, user.getId(), LocalDateTime.now(), issuedRefreshToken.expiresAt()));

        return new AuthenticatedUserResult(accessToken, issuedRefreshToken.token(), user.getId(), user.getEmail(), user.getRole());
    }

    private void revokeAllActiveTokensFor(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        for (RefreshToken active : refreshTokenRepository.findActiveByUserId(userId)) {
            active.revoke(now, null);
            refreshTokenRepository.save(active);
        }
    }
}
