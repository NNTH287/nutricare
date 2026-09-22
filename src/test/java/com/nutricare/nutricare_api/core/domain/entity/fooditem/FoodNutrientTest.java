package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodNutrientTest {

    @Test
    void givenNonPositiveAmount_whenConstructed_thenThrowsInvalidFoodNutrientException() {
        assertThatThrownBy(() -> new FoodNutrient(1, 2, 0.0))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }

    @Test
    void givenNullAmount_whenConstructed_thenThrowsInvalidFoodNutrientException() {
        assertThatThrownBy(() -> new FoodNutrient(1, 2, null))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }

    @Test
    void givenSameFoodItemIdAndNutrientId_whenComparedForEquality_thenTheyAreEqualRegardlessOfAmount() {
        FoodNutrient first = new FoodNutrient(1, 2, 10.0);
        FoodNutrient second = new FoodNutrient(1, 2, 99.0);

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void givenDifferentNutrientId_whenComparedForEquality_thenTheyAreNotEqual() {
        FoodNutrient first = new FoodNutrient(1, 2, 10.0);
        FoodNutrient second = new FoodNutrient(1, 3, 10.0);

        assertThat(first).isNotEqualTo(second);
    }
}
