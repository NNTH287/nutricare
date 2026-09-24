package com.nutricare.nutricare_api.core.domain.entity.intake;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import com.nutricare.nutricare_api.core.domain.event.DomainEvent;
import com.nutricare.nutricare_api.core.domain.event.IntakeEntryLogged;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

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
    void givenNewLog_whenAddEntryIsCalled_thenRegistersExactlyOneIntakeEntryLoggedEvent() {
        LocalDate date = LocalDate.now().minusDays(1);
        IntakeLog log = IntakeLog.create(1, date);

        log.addEntry(5, 100.0, MealSlot.BREAKFAST);

        List<DomainEvent> events = log.pullDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.get(0)).isInstanceOf(IntakeEntryLogged.class);
        IntakeEntryLogged event = (IntakeEntryLogged) events.get(0);
        assertThat(event.profileId()).isEqualTo(1);
        assertThat(event.foodItemId()).isEqualTo(5);
        assertThat(event.quantityG()).isEqualTo(100.0);
        assertThat(event.date()).isEqualTo(date);
    }

    @Test
    void givenPulledDomainEvents_whenPullDomainEventsIsCalledAgain_thenReturnsEmpty() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));
        log.addEntry(5, 100.0, MealSlot.BREAKFAST);
        log.pullDomainEvents();

        assertThat(log.pullDomainEvents()).isEmpty();
    }

    @Test
    void givenTwoEntriesAdded_whenPullDomainEventsIsCalled_thenReturnsBothEventsInOrder() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));

        log.addEntry(5, 100.0, MealSlot.BREAKFAST);
        log.addEntry(7, 50.0, MealSlot.LUNCH);

        List<DomainEvent> events = log.pullDomainEvents();
        assertThat(events).hasSize(2);
        assertThat(((IntakeEntryLogged) events.get(0)).foodItemId()).isEqualTo(5);
        assertThat(((IntakeEntryLogged) events.get(1)).foodItemId()).isEqualTo(7);
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
