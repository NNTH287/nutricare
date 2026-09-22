package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.dto.RegisterUserCommand;
import com.nutricare.nutricare_api.core.application.port.out.PasswordHasher;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.DuplicateEmailException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.core.domain.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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

    private RegisterUserService service;

    @BeforeEach
    void setUp() {
        service = new RegisterUserService(userRepository, passwordHasher, tokenIssuer);
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
    void givenNewEmail_whenRegisterIsCalled_thenHashesPasswordSavesUserAndIssuesToken() {
        RegisterUserCommand command = new RegisterUserCommand("user@example.com", "raw-password");
        User savedUser = User.reconstitute(1, "user@example.com", "hashed-password", Role.USER,
                java.time.LocalDateTime.now(), java.time.LocalDateTime.now());
        when(userRepository.existsByEmail("user@example.com")).thenReturn(false);
        when(passwordHasher.hash("raw-password")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER)).thenReturn("jwt-token");

        AuthenticatedUserResult result = service.register(command);

        assertThat(result.token()).isEqualTo("jwt-token");
        assertThat(result.userId()).isEqualTo(1);
        assertThat(result.email()).isEqualTo("user@example.com");
        assertThat(result.role()).isEqualTo(Role.USER);
    }
}
