package com.nutricare.nutricare_api.core.domain.entity.intake;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IntakeEntryTest {

    @Test
    void givenNullFoodItemId_whenCreateIsCalled_thenThrowsInvalidIntakeEntryException() {
        assertThatThrownBy(() -> IntakeEntry.create(null, 100.0, MealSlot.BREAKFAST))
                .isInstanceOf(InvalidIntakeEntryException.class);
    }

    @Test
    void givenNonPositiveQuantity_whenCreateIsCalled_thenThrowsInvalidIntakeEntryException() {
        assertThatThrownBy(() -> IntakeEntry.create(1, 0.0, MealSlot.BREAKFAST))
                .isInstanceOf(InvalidIntakeEntryException.class);
    }

    @Test
    void givenValidInputs_whenCreateIsCalled_thenEntryIsCreatedWithGivenAttributes() {
        IntakeEntry entry = IntakeEntry.create(1, 100.0, MealSlot.LUNCH);

        assertThat(entry.getFoodItemId()).isEqualTo(1);
        assertThat(entry.getQuantityG()).isEqualTo(100.0);
        assertThat(entry.getMealSlot()).isEqualTo(MealSlot.LUNCH);
    }

    @Test
    void givenNonPositiveQuantity_whenSetQuantityGIsCalled_thenThrowsInvalidIntakeEntryException() {
        IntakeEntry entry = IntakeEntry.create(1, 100.0, MealSlot.LUNCH);

        assertThatThrownBy(() -> entry.setQuantityG(-1.0))
                .isInstanceOf(InvalidIntakeEntryException.class);
    }
}
