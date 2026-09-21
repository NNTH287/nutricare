package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.dto.RegisterUserCommand;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.DuplicateEmailException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.core.domain.entity.user.User;

public class RegisterUserService implements RegisterUserUseCase {
    private final UserRepository userRepository;
    private final PasswordHasher passwordHasher;
    private final TokenIssuer tokenIssuer;

    public RegisterUserService(UserRepository userRepository, PasswordHasher passwordHasher, TokenIssuer tokenIssuer) {
        this.userRepository = userRepository;
        this.passwordHasher = passwordHasher;
        this.tokenIssuer = tokenIssuer;
    }

    @Override
    public AuthenticatedUserResult register(RegisterUserCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            throw new DuplicateEmailException("Email is already registered: " + command.email());
        }

        String passwordHash = passwordHasher.hash(command.rawPassword());
        User user = userRepository.save(User.register(command.email(), passwordHash, Role.USER));
        String token = tokenIssuer.issueAccessToken(user.getId(), user.getEmail(), user.getRole());

        return new AuthenticatedUserResult(token, user.getId(), user.getEmail(), user.getRole());
    }
}
