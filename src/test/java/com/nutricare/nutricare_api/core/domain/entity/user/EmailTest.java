package com.nutricare.nutricare_api.core.domain.entity.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class EmailTest {

    @ParameterizedTest
    @ValueSource(strings = {"user@example.com", "USER@EXAMPLE.COM", "first.last@sub.example.co"})
    void givenWellFormedAddress_whenEmailIsConstructed_thenValueIsNormalizedToLowerCase(String rawEmail) {
        Email email = new Email(rawEmail);

        assertThat(email.getValue()).isEqualTo(rawEmail.toLowerCase());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "not-an-email", "missing-domain@", "@missing-local.com", "spaces in@email.com"})
    void givenMalformedAddress_whenEmailIsConstructed_thenThrowsInvalidEmailException(String rawEmail) {
        assertThatThrownBy(() -> new Email(rawEmail))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void givenNullAddress_whenEmailIsConstructed_thenThrowsInvalidEmailException() {
        assertThatThrownBy(() -> new Email(null))
                .isInstanceOf(InvalidEmailException.class);
    }

    @Test
    void givenTwoEmailsWithDifferentCasingOfSameAddress_whenComparedForEquality_thenTheyAreEqual() {
        Email lower = new Email("user@example.com");
        Email upper = new Email("USER@EXAMPLE.COM");

        assertThat(lower).isEqualTo(upper);
        assertThat(lower.hashCode()).isEqualTo(upper.hashCode());
    }
}
