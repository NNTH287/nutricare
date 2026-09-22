package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NutrientAmountTest {

    @Test
    void givenNullNutrientId_whenConstructed_thenThrowsInvalidFoodNutrientException() {
        assertThatThrownBy(() -> new NutrientAmount(null, 10.0))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }

    @Test
    void givenNullAmount_whenConstructed_thenThrowsInvalidFoodNutrientException() {
        assertThatThrownBy(() -> new NutrientAmount(1, null))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }

    @Test
    void givenNonPositiveAmount_whenConstructed_thenThrowsInvalidFoodNutrientException() {
        assertThatThrownBy(() -> new NutrientAmount(1, 0.0))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }
}
