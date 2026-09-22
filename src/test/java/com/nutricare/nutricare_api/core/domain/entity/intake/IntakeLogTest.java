package com.nutricare.nutricare_api.core.domain.entity.intake;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class IntakeLogTest {

    @Test
    void givenNullProfileId_whenCreateIsCalled_thenThrowsInvalidIntakeLogException() {
        assertThatThrownBy(() -> IntakeLog.create(null, LocalDate.now().minusDays(1)))
                .isInstanceOf(InvalidIntakeLogException.class);
    }

    @Test
    void givenNullLogDate_whenCreateIsCalled_thenThrowsInvalidIntakeLogException() {
        assertThatThrownBy(() -> IntakeLog.create(1, null))
                .isInstanceOf(InvalidIntakeLogException.class);
    }

    @Test
    void givenFutureLogDate_whenCreateIsCalled_thenThrowsInvalidIntakeLogException() {
        assertThatThrownBy(() -> IntakeLog.create(1, LocalDate.now().plusDays(1)))
                .isInstanceOf(InvalidIntakeLogException.class);
    }

    @Test
    void givenTodayAsLogDate_whenCreateIsCalled_thenNoExceptionIsThrown() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now());

        assertThat(log.getLogDate()).isEqualTo(LocalDate.now());
    }

    @Test
    void givenNewLog_whenAddEntryIsCalled_thenEntryIsAddedToTheLog() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));

        IntakeEntry entry = log.addEntry(5, 100.0, MealSlot.BREAKFAST);

        assertThat(log.getEntries()).containsExactly(entry);
    }

    @Test
    void givenLogWithEntry_whenGetEntryIsCalledWithUnknownId_thenThrowsInvalidIntakeLogException() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));

        assertThatThrownBy(() -> log.getEntry(999))
                .isInstanceOf(InvalidIntakeLogException.class);
    }

    @Test
    void givenExistingEntryId_whenUpdateEntryMealSlotIsCalled_thenMealSlotIsUpdatedInPlace() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1),
                java.util.List.of(IntakeEntry.reconstitute(10, 5, 100.0, MealSlot.BREAKFAST)));

        log.updateEntryMealSlot(10, MealSlot.DINNER);

        assertThat(log.getEntry(10).getMealSlot()).isEqualTo(MealSlot.DINNER);
    }

    @Test
    void givenExistingEntryId_whenUpdateEntryQuantityIsCalled_thenQuantityIsUpdatedInPlace() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1),
                java.util.List.of(IntakeEntry.reconstitute(10, 5, 100.0, MealSlot.BREAKFAST)));

        log.updateEntryQuantity(10, 150.0);

        assertThat(log.getEntry(10).getQuantityG()).isEqualTo(150.0);
    }

    @Test
    void givenExistingEntryId_whenRemoveEntryIsCalled_thenEntryIsRemovedFromTheLog() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1),
                java.util.List.of(IntakeEntry.reconstitute(10, 5, 100.0, MealSlot.BREAKFAST)));

        log.removeEntry(10);

        assertThat(log.getEntries()).isEmpty();
    }
}
