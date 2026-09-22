package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NutrientTest {

    @Test
    void givenBlankCode_whenCreateIsCalled_thenThrowsInvalidNutrientException() {
        assertThatThrownBy(() -> Nutrient.create(" ", "Protein", "g"))
                .isInstanceOf(InvalidNutrientException.class);
    }

    @Test
    void givenBlankName_whenCreateIsCalled_thenThrowsInvalidNutrientException() {
        assertThatThrownBy(() -> Nutrient.create("protein", " ", "g"))
                .isInstanceOf(InvalidNutrientException.class);
    }

    @Test
    void givenBlankUnit_whenCreateIsCalled_thenThrowsInvalidNutrientException() {
        assertThatThrownBy(() -> Nutrient.create("protein", "Protein", " "))
                .isInstanceOf(InvalidNutrientException.class);
    }

    @Test
    void givenValidInputs_whenCreateIsCalled_thenNutrientIsCreatedWithGivenAttributes() {
        Nutrient nutrient = Nutrient.create("protein", "Protein", "g");

        assertThat(nutrient.getCode()).isEqualTo("protein");
        assertThat(nutrient.getName()).isEqualTo("Protein");
        assertThat(nutrient.getUnit()).isEqualTo("g");
    }

    @Test
    void givenBlankCode_whenSetCodeIsCalled_thenThrowsInvalidNutrientException() {
        Nutrient nutrient = Nutrient.create("protein", "Protein", "g");

        assertThatThrownBy(() -> nutrient.setCode(" "))
                .isInstanceOf(InvalidNutrientException.class);
    }

    @Test
    void givenBlankName_whenSetNameIsCalled_thenThrowsInvalidNutrientException() {
        Nutrient nutrient = Nutrient.create("protein", "Protein", "g");

        assertThatThrownBy(() -> nutrient.setName(" "))
                .isInstanceOf(InvalidNutrientException.class);
    }

    @Test
    void givenBlankUnit_whenSetUnitIsCalled_thenThrowsInvalidNutrientException() {
        Nutrient nutrient = Nutrient.create("protein", "Protein", "g");

        assertThatThrownBy(() -> nutrient.setUnit(" "))
                .isInstanceOf(InvalidNutrientException.class);
    }
}
