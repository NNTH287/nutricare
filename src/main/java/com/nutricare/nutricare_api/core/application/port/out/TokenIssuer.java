package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

import java.util.Optional;
import java.util.UUID;

public interface TokenIssuer {
    String issueAccessToken(Integer userId, String email, Role role);

    IssuedRefreshToken issueRefreshToken(Integer userId, UUID jti);

    Optional<VerifiedRefreshToken> verifyRefreshToken(String token);
}
