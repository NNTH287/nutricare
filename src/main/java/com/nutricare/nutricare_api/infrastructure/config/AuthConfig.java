package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RefreshTokenUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.application.service.AuthenticateUserService;
import com.nutricare.nutricare_api.core.application.service.RefreshTokenService;
import com.nutricare.nutricare_api.core.application.service.RegisterUserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                                                     TokenIssuer tokenIssuer, RefreshTokenRepository refreshTokenRepository) {
        return new RegisterUserService(userRepository, passwordHasher, tokenIssuer, refreshTokenRepository);
    }

    @Bean
    public AuthenticateUserUseCase authenticateUserUseCase(UserRepository userRepository, PasswordHasher passwordHasher,
                                                             TokenIssuer tokenIssuer, RefreshTokenRepository refreshTokenRepository) {
        return new AuthenticateUserService(userRepository, passwordHasher, tokenIssuer, refreshTokenRepository);
    }

    @Bean
    public RefreshTokenUseCase refreshTokenUseCase(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository,
                                                     TokenIssuer tokenIssuer) {
        return new RefreshTokenService(refreshTokenRepository, userRepository, tokenIssuer);
    }
}
