package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogHeaderResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.mapper.IntakeLoggingMapper;
import com.nutricare.nutricare_api.core.application.port.out.DomainEventPublisher;
import com.nutricare.nutricare_api.core.application.port.out.IntakeLogRepository;
import com.nutricare.nutricare_api.core.application.port.out.ProfileRepository;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import com.nutricare.nutricare_api.core.domain.entity.profile.*;
import com.nutricare.nutricare_api.core.domain.event.DomainEvent;
import com.nutricare.nutricare_api.core.domain.event.IntakeEntryLogged;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogIntakeServiceTest {

    @Mock
    private IntakeLogRepository intakeLogRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private DomainEventPublisher domainEventPublisher;

    private LogIntakeService service;

    private static Profile ownedProfile(Integer id, Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(id, userId, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, null, Set.of(), now, now);
    }

    @BeforeEach
    void setUp() {
        service = new LogIntakeService(intakeLogRepository, profileRepository, domainEventPublisher, new IntakeLoggingMapper());
    }

    @Test
    void givenNonOwnerAuthenticatedUser_whenCreateLogIsCalled_thenThrowsProfileAccessDeniedExceptionAndNeverTouchesRepository() {
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));

        assertThatThrownBy(() -> service.createLog(1, LocalDate.now().minusDays(1), 999))
                .isInstanceOf(ProfileAccessDeniedException.class);

        verifyNoInteractions(intakeLogRepository);
    }

    @Test
    void givenNoExistingLogForDate_whenCreateLogIsCalled_thenCreatesAndSavesANewLog() {
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));
        LocalDate date = LocalDate.now().minusDays(1);
        when(intakeLogRepository.findByProfileIdAndDate(1, date)).thenReturn(Optional.empty());
        when(intakeLogRepository.save(any(IntakeLog.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IntakeLogHeaderResult result = service.createLog(1, date, 10);

        assertThat(result.profileId()).isEqualTo(1);
        assertThat(result.logDate()).isEqualTo(date);
        verify(intakeLogRepository).save(any(IntakeLog.class));
    }

    @Test
    void givenExistingLogForDate_whenCreateLogIsCalled_thenReturnsExistingLogWithoutSavingANewOne() {
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));
        LocalDate date = LocalDate.now().minusDays(1);
        IntakeLog existing = IntakeLog.reconstitute(5, 1, date, java.util.List.of());
        when(intakeLogRepository.findByProfileIdAndDate(1, date)).thenReturn(Optional.of(existing));

        IntakeLogHeaderResult result = service.createLog(1, date, 10);

        assertThat(result.id()).isEqualTo(5);
        verify(intakeLogRepository, never()).save(any());
    }

    @Test
    void givenNonOwnerAuthenticatedUser_whenCreateEntryIsCalled_thenThrowsProfileAccessDeniedExceptionAndNeverSaves() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1), java.util.List.of());
        when(intakeLogRepository.findById(1)).thenReturn(Optional.of(log));
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));
        CreateIntakeEntryCommand command = new CreateIntakeEntryCommand(1, 5, 100.0, MealSlot.BREAKFAST);

        assertThatThrownBy(() -> service.createEntry(command, 999))
                .isInstanceOf(ProfileAccessDeniedException.class);

        verify(intakeLogRepository, never()).save(any());
    }

    @Test
    void givenOwnedLog_whenCreateEntryIsCalled_thenAddsEntryAndSavesTheLog() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1), java.util.List.of());
        when(intakeLogRepository.findById(1)).thenReturn(Optional.of(log));
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));
        CreateIntakeEntryCommand command = new CreateIntakeEntryCommand(1, 5, 100.0, MealSlot.BREAKFAST);

        IntakeEntryResult result = service.createEntry(command, 10);

        assertThat(result.foodItemId()).isEqualTo(5);
        assertThat(result.quantityG()).isEqualTo(100.0);
        verify(intakeLogRepository).save(log);

        ArgumentCaptor<List<DomainEvent>> eventsCaptor = ArgumentCaptor.forClass(List.class);
        verify(domainEventPublisher).publish(eventsCaptor.capture());
        assertThat(eventsCaptor.getValue()).hasSize(1);
        assertThat(eventsCaptor.getValue().get(0)).isInstanceOf(IntakeEntryLogged.class);
        IntakeEntryLogged event = (IntakeEntryLogged) eventsCaptor.getValue().get(0);
        assertThat(event.foodItemId()).isEqualTo(5);
        assertThat(event.quantityG()).isEqualTo(100.0);
    }

    @Test
    void givenOwnedLogWithExistingEntry_whenUpdateEntryIsCalled_thenAppliesChangesAndSavesTheLog() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));
        var entry = log.addEntry(5, 100.0, MealSlot.BREAKFAST);
        when(intakeLogRepository.findById(1)).thenReturn(Optional.of(log));
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));
        UpdateIntakeEntryCommand command = new UpdateIntakeEntryCommand(1, entry.getId(), 200.0, MealSlot.DINNER);

        IntakeEntryResult result = service.updateEntry(command, 10);

        assertThat(result.quantityG()).isEqualTo(200.0);
        assertThat(result.mealSlot()).isEqualTo(MealSlot.DINNER);
        verify(intakeLogRepository).save(log);
    }

    @Test
    void givenOwnedLog_whenDeleteEntryByIdIsCalled_thenRemovesEntryAndSavesTheLog() {
        IntakeLog log = IntakeLog.create(1, LocalDate.now().minusDays(1));
        var entry = log.addEntry(5, 100.0, MealSlot.BREAKFAST);
        when(intakeLogRepository.findById(1)).thenReturn(Optional.of(log));
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));

        service.deleteEntryById(1, entry.getId(), 10);

        assertThat(log.getEntries()).isEmpty();
        verify(intakeLogRepository).save(log);
    }

    @Test
    void givenOwnedLog_whenDeleteLogByIdIsCalled_thenDelegatesDeletionToRepository() {
        IntakeLog log = IntakeLog.reconstitute(1, 1, LocalDate.now().minusDays(1), java.util.List.of());
        when(intakeLogRepository.findById(1)).thenReturn(Optional.of(log));
        when(profileRepository.getById(1)).thenReturn(ownedProfile(1, 10));

        service.deleteLogById(1, 10);

        verify(intakeLogRepository).deleteById(1);
    }
}
