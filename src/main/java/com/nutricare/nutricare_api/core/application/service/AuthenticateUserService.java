package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.User;

public class AuthenticateUserService implements AuthenticateUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    public AuthenticateUserService(UserRepository userRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public AuthenticatedUserResult login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordHasher.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        String token = tokenIssuer.issueAccessToken(user.getId(), user.getEmail(), user.getRole());
        return new AuthenticatedUserResult(token, user.getId(), user.getEmail(), user.getRole());
    }
}
