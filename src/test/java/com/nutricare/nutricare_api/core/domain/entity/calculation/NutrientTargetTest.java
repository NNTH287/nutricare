package com.nutricare.nutricare_api.core.domain.entity.calculation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NutrientTargetTest {

    @Test
    void givenNullNutrientId_whenConstructed_thenThrowsInvalidNutrientTargetException() {
        assertThatThrownBy(() -> new NutrientTarget(null, 10.0, null))
                .isInstanceOf(InvalidNutrientTargetException.class);
    }

    @Test
    void givenNonPositiveTargetAmount_whenConstructed_thenThrowsInvalidNutrientTargetException() {
        assertThatThrownBy(() -> new NutrientTarget(1, 0.0, null))
                .isInstanceOf(InvalidNutrientTargetException.class);
    }

    @Test
    void givenNonPositiveMaxAmount_whenConstructed_thenThrowsInvalidNutrientTargetException() {
        assertThatThrownBy(() -> new NutrientTarget(1, 10.0, 0.0))
                .isInstanceOf(InvalidNutrientTargetException.class);
    }

    @Test
    void givenNullMaxAmount_whenConstructed_thenNoExceptionIsThrown() {
        assertThatCode(() -> new NutrientTarget(1, 10.0, null))
                .doesNotThrowAnyException();
    }
}
