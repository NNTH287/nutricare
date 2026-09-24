package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.out.ApplicationMetrics;
import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;
    private final RefreshTokenRepository refreshTokenRepository;
    private final ApplicationMetrics metrics;

    public AuthenticateUserService(UserRepository userRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer,
                                    RefreshTokenRepository refreshTokenRepository, ApplicationMetrics metrics) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
        this.refreshTokenRepository = refreshTokenRepository;
        this.metrics = metrics;
    }

    @Override
    public AuthenticatedUserResult login(String email, String rawPassword) {
        Optional<User> maybeUser = userRepository.findByEmail(email);
        if (maybeUser.isEmpty()) {
            metrics.recordLoginFailure("unknown_email");
            throw new InvalidCredentialsException("Invalid email or password");
        }
        User user = maybeUser.get();

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            metrics.recordLoginFailure("wrong_password");
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String accessToken = tokenIssuer.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        UUID jti = UUID.randomUUID();
        IssuedRefreshToken issuedRefreshToken = tokenIssuer.issueRefreshToken(user.getId(), jti);
        refreshTokenRepository.save(RefreshToken.issue(jti, user.getId(), LocalDateTime.now(), issuedRefreshToken.expiresAt()));

        return new AuthenticatedUserResult(accessToken, issuedRefreshToken.token(), user.getId(), user.getEmail(), user.getRole());
    }
}
