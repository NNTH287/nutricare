package com.nutricare.nutricare_api.core.domain.entity.user;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class UserTest {

    @Test
    void givenMalformedEmail_whenRegisterIsCalled_thenThrowsInvalidEmailException() {
        assertThatThrownBy(() -> User.register("not-an-email", "hash", Role.USER))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void givenBlankPasswordHash_whenRegisterIsCalled_thenThrowsInvalidUserException() {
        assertThatThrownBy(() -> User.register("user@example.com", "  ", Role.USER))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void givenNullRole_whenRegisterIsCalled_thenThrowsInvalidUserException() {
        assertThatThrownBy(() -> User.register("user@example.com", "hash", null))
                .isInstanceOf(InvalidUserException.class);
    }

    @Test
    void givenValidInputs_whenRegisterIsCalled_thenUserIsCreatedWithNormalizedEmail() {
        User user = User.register("User@Example.com", "hash", Role.USER);

        assertThat(user.getEmail()).isEqualTo("user@example.com");
        assertThat(user.getPasswordHash()).isEqualTo("hash");
        assertThat(user.getRole()).isEqualTo(Role.USER);
    }
}
