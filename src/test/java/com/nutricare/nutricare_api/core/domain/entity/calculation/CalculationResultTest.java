package com.nutricare.nutricare_api.core.domain.entity.calculation;

import com.nutricare.nutricare_api.core.domain.entity.profile.ActivityLevel;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CalculationResultTest {

    private static Profile profileUpdatedAt(LocalDateTime updatedAt) {
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, null,
                Set.of(), updatedAt.minusDays(1), updatedAt);
    }

    @Test
    void givenNullProfileId_whenCreateIsCalled_thenThrowsInvalidCalculationResultException() {
        assertThatThrownBy(() -> CalculationResult.create(null, 1, 2000.0, Set.of(), Set.of()))
                .isInstanceOf(InvalidCalculationResultException.class);
    }

    @Test
    void givenNullStandardId_whenCreateIsCalled_thenThrowsInvalidCalculationResultException() {
        assertThatThrownBy(() -> CalculationResult.create(1, null, 2000.0, Set.of(), Set.of()))
                .isInstanceOf(InvalidCalculationResultException.class);
    }

    @Test
    void givenNonPositiveCalorieTarget_whenCreateIsCalled_thenThrowsInvalidCalculationResultException() {
        assertThatThrownBy(() -> CalculationResult.create(1, 1, 0.0, Set.of(), Set.of()))
                .isInstanceOf(InvalidCalculationResultException.class);
    }

    @Test
    void givenCalculatedAtBeforeProfileWasLastUpdated_whenIsStillValidForIsCalled_thenReturnsFalse() {
        LocalDateTime profileUpdatedAt = LocalDateTime.now();
        Profile profile = profileUpdatedAt(profileUpdatedAt);
        CalculationResult result = CalculationResult.constitute(1, 1, 1, profileUpdatedAt.minusDays(1),
                2000.0, Set.of(), Set.of());

        assertThat(result.isStillValidFor(profile)).isFalse();
    }

    @Test
    void givenCalculatedAtAfterProfileWasLastUpdated_whenIsStillValidForIsCalled_thenReturnsTrue() {
        LocalDateTime profileUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = profileUpdatedAt(profileUpdatedAt);
        CalculationResult result = CalculationResult.constitute(1, 1, 1, profileUpdatedAt.plusDays(1),
                2000.0, Set.of(), Set.of());

        assertThat(result.isStillValidFor(profile)).isTrue();
    }

    @Test
    void givenCalculatedAtEqualToProfileUpdatedAt_whenIsStillValidForIsCalled_thenReturnsTrue() {
        LocalDateTime timestamp = LocalDateTime.now();
        Profile profile = profileUpdatedAt(timestamp);
        CalculationResult result = CalculationResult.constitute(1, 1, 1, timestamp, 2000.0, Set.of(), Set.of());

        assertThat(result.isStillValidFor(profile)).isTrue();
    }
}
