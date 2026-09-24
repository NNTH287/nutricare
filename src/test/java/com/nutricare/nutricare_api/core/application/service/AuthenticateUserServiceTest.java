package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.out.ApplicationMetrics;
import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.core.domain.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthenticateUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private ApplicationMetrics metrics;

    private AuthenticateUserService service;

    @BeforeEach
    void setUp() {
        service = new AuthenticateUserService(userRepository, passwordHasher, tokenIssuer, refreshTokenRepository, metrics);
    }

    @Test
    void givenUnknownEmail_whenLoginIsCalled_thenThrowsInvalidCredentialsExceptionAndRecordsFailure() {
        when(userRepository.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.login("unknown@example.com", "raw-password"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(metrics).recordLoginFailure("unknown_email");
    }

    @Test
    void givenWrongPassword_whenLoginIsCalled_thenThrowsInvalidCredentialsExceptionAndRecordsFailure() {
        User user = User.reconstitute(1, "user@example.com", "hashed-password", Role.USER,
                LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("wrong-password", "hashed-password")).thenReturn(false);

        assertThatThrownBy(() -> service.login("user@example.com", "wrong-password"))
                .isInstanceOf(InvalidCredentialsException.class);

        verify(metrics).recordLoginFailure("wrong_password");
    }

    @Test
    void givenCorrectCredentials_whenLoginIsCalled_thenReturnsResultWithIssuedTokensAndPersistsRefreshToken() {
        User user = User.reconstitute(1, "user@example.com", "hashed-password", Role.USER,
                LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordHasher.matches("raw-password", "hashed-password")).thenReturn(true);
        when(tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER)).thenReturn("access-jwt");
        when(tokenIssuer.issueRefreshToken(eq(1), any(UUID.class)))
                .thenReturn(new IssuedRefreshToken("refresh-jwt", LocalDateTime.now().plusDays(7)));

        AuthenticatedUserResult result = service.login("user@example.com", "raw-password");

        assertThat(result.accessToken()).isEqualTo("access-jwt");
        assertThat(result.refreshToken()).isEqualTo("refresh-jwt");
        assertThat(result.userId()).isEqualTo(1);
        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.role()).isEqualTo(Role.USER);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1);
    }
}
