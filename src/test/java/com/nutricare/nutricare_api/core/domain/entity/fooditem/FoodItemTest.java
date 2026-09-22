package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FoodItemTest {

    @Test
    void givenBlankName_whenCreateIsCalled_thenThrowsInvalidFoodItemException() {
        assertThatThrownBy(() -> FoodItem.create(" ", "category", 100.0, Set.of(), null, Set.of()))
                .isInstanceOf(InvalidFoodItemException.class);
    }

    @Test
    void givenNonPositiveServingSize_whenCreateIsCalled_thenThrowsInvalidFoodItemException() {
        assertThatThrownBy(() -> FoodItem.create("Rice", "category", 0.0, Set.of(), null, Set.of()))
                .isInstanceOf(InvalidFoodItemException.class);
    }

    @Test
    void givenValidInputs_whenCreateIsCalled_thenFoodItemIsCreatedWithGivenAttributes() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of("staple"), 5, Set.of());

        assertThat(item.getName()).isEqualTo("Rice");
        assertThat(item.getCategory()).isEqualTo("Grain");
        assertThat(item.getServingSizeG()).isEqualTo(100.0);
        assertThat(item.getOwnerProfileId()).isEqualTo(5);
    }

    @Test
    void givenNutrientAmountsWithDuplicateNutrientId_whenCreateIsCalled_thenThrowsInvalidFoodNutrientException() {
        Set<NutrientAmount> duplicateAmounts = Set.of(
                new NutrientAmount(1, 10.0),
                new NutrientAmount(1, 20.0));

        // NutrientAmount is a record with structural equality, so two entries with the
        // same nutrientId but different amounts are distinct Set elements and both reach the check.
        assertThatThrownBy(() -> FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, duplicateAmounts))
                .isInstanceOf(InvalidFoodNutrientException.class);
    }

    @Test
    void givenNutrientAmountsWithDistinctNutrientIds_whenReplaceNutrientsIsCalled_thenNutrientsAreReplaced() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());

        item.replaceNutrients(Set.of(new NutrientAmount(1, 10.0), new NutrientAmount(2, 20.0)));

        assertThat(item.getNutrients()).hasSize(2);
    }

    @Test
    void givenNullOwnerProfileId_whenIsVisibleToIsCalled_thenReturnsTrueForAnyProfile() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());

        assertThat(item.isVisibleTo(999)).isTrue();
    }

    @Test
    void givenMatchingOwnerProfileId_whenIsVisibleToIsCalled_thenReturnsTrue() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), 5, Set.of());

        assertThat(item.isVisibleTo(5)).isTrue();
    }

    @Test
    void givenMismatchedOwnerProfileId_whenIsVisibleToIsCalled_thenReturnsFalse() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), 5, Set.of());

        assertThat(item.isVisibleTo(999)).isFalse();
    }

    @Test
    void givenBlankName_whenSetNameIsCalled_thenThrowsInvalidFoodItemException() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());

        assertThatThrownBy(() -> item.setName(""))
                .isInstanceOf(InvalidFoodItemException.class);
    }

    @Test
    void givenNonPositiveServingSize_whenSetServingSizeGIsCalled_thenThrowsInvalidFoodItemException() {
        FoodItem item = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());

        assertThatThrownBy(() -> item.setServingSizeG(-1.0))
                .isInstanceOf(InvalidFoodItemException.class);
    }
}
