package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.dto.RegisterUserCommand;
import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.DuplicateEmailException;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @Mock
    private TokenIssuer tokenIssuer;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        service = new RegisterUserService(userRepository, passwordHasher, tokenIssuer, refreshTokenRepository);
    }

    @Test
    void givenEmailAlreadyRegistered_whenRegisterIsCalled_thenThrowsDuplicateEmailExceptionAndNeverSaves() {
        RegisterUserCommand command = new RegisterUserCommand("user@example.com", "raw-password");
        when(userRepository.existsByEmail("user@example.com")).thenReturn(true);

        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(DuplicateEmailException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void givenNewEmail_whenRegisterIsCalled_thenHashesPasswordSavesUserAndIssuesTokens() {
        RegisterUserCommand command = new RegisterUserCommand("user@example.com", "raw-password");
        User savedUser = User.reconstitute(1, "user@example.com", "hashed-password", Role.USER,
                LocalDateTime.now(), LocalDateTime.now());
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordHasher.hash("raw-password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER)).thenReturn("access-jwt");
        when(tokenIssuer.issueRefreshToken(eq(1), any(UUID.class)))
                .thenReturn(new IssuedRefreshToken("refresh-jwt", LocalDateTime.now().plusDays(7)));

        AuthenticatedUserResult result = service.register(command);

        assertThat(result.accessToken()).isEqualTo("access-jwt");
        assertThat(result.refreshToken()).isEqualTo("refresh-jwt");
        assertThat(result.userId()).isEqualTo(1);
        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.role()).isEqualTo(Role.USER);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getPasswordHash()).isEqualTo("hashed-password");

        ArgumentCaptor<RefreshToken> refreshTokenCaptor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(refreshTokenCaptor.capture());
        assertThat(refreshTokenCaptor.getValue().getUserId()).isEqualTo(1);
    }
}
