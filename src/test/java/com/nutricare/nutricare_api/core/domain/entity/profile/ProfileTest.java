package com.nutricare.nutricare_api.core.domain.entity.profile;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProfileTest {

    private static Profile reconstitutedAdult(LocalDateTime updatedAt) {
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, null,
                Set.of(), updatedAt.minusDays(1), updatedAt);
    }

    @Test
    void givenPregnantGroupType_whenCreateIsCalled_thenThrowsInvalidTrimesterException() {
        assertThatThrownBy(() -> Profile.create(1, "Jane", GroupType.PREGNANT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, Set.of()))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenNonPregnantGroupType_whenCreateIsCalled_thenProfileHasNullTrimester() {
        Profile profile = Profile.create(1, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, Set.of());

        assertThat(profile.getTrimester()).isNull();
        assertThat(profile.getGroupType()).isEqualTo(GroupType.ADULT);
    }

    @Test
    void givenTrimesterOutsideRange_whenCreatePregnantIsCalled_thenThrowsInvalidTrimesterException() {
        assertThatThrownBy(() -> Profile.createPregnant(1, "Jane", SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, Set.of(), 4))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenTrimesterWithinRange_whenCreatePregnantIsCalled_thenProfileHasPregnantGroupAndTrimester() {
        Profile profile = Profile.createPregnant(1, "Jane", SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, Set.of(), 2);

        assertThat(profile.getGroupType()).isEqualTo(GroupType.PREGNANT);
        assertThat(profile.getTrimester()).isEqualTo(2);
    }

    @Test
    void givenNonPregnantProfile_whenSetTrimesterIsCalled_thenThrowsInvalidTrimesterException() {
        Profile profile = reconstitutedAdult(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> profile.setTrimester(1))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenPregnantProfile_whenSetTrimesterWithValidValue_thenTrimesterIsUpdatedAndUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = Profile.reconstitute(1, 10, "Jane", GroupType.PREGNANT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, 1,
                Set.of(), originalUpdatedAt.minusDays(1), originalUpdatedAt);

        profile.setTrimester(2);

        assertThat(profile.getTrimester()).isEqualTo(2);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetNameIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setName("New Name");

        assertThat(profile.getName()).isEqualTo("New Name");
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetSexTypeIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setSexType(SexType.MALE);

        assertThat(profile.getSexType()).isEqualTo(SexType.MALE);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetBirthDateIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);
        LocalDate newBirthDate = LocalDate.of(1995, 5, 5);

        profile.setBirthDate(newBirthDate);

        assertThat(profile.getBirthDate()).isEqualTo(newBirthDate);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetWeightKgIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setWeightKg(70.0);

        assertThat(profile.getWeightKg()).isEqualTo(70.0);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetHeightCmIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setHeightCm(170.0);

        assertThat(profile.getHeightCm()).isEqualTo(170.0);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetActivityLevelIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setActivityLevel(ActivityLevel.VERY_ACTIVE);

        assertThat(profile.getActivityLevel()).isEqualTo(ActivityLevel.VERY_ACTIVE);
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenExistingProfile_whenSetConditionsIsCalled_thenUpdatedAtIsBumped() {
        LocalDateTime originalUpdatedAt = LocalDateTime.now().minusDays(1);
        Profile profile = reconstitutedAdult(originalUpdatedAt);

        profile.setConditions(Set.of("diabetes"));

        assertThat(profile.getConditions()).containsExactly("diabetes");
        assertThat(profile.getUpdatedAt()).isAfter(originalUpdatedAt);
    }

    @Test
    void givenNullConditions_whenSetConditionsIsCalled_thenConditionsBecomeEmptySet() {
        Profile profile = reconstitutedAdult(LocalDateTime.now().minusDays(1));

        profile.setConditions(null);

        assertThat(profile.getConditions()).isEmpty();
    }

    @Test
    void givenMatchingUserId_whenVerifyOwnedByIsCalled_thenNoExceptionIsThrown() {
        Profile profile = reconstitutedAdult(LocalDateTime.now().minusDays(1));

        assertThat(profile.getUserId()).isEqualTo(10);
        profile.verifyOwnedBy(10);
    }

    @Test
    void givenMismatchedUserId_whenVerifyOwnedByIsCalled_thenThrowsProfileAccessDeniedException() {
        Profile profile = reconstitutedAdult(LocalDateTime.now().minusDays(1));

        assertThatThrownBy(() -> profile.verifyOwnedBy(999))
                .isInstanceOf(ProfileAccessDeniedException.class);
    }

    @Test
    void givenFixedBirthDate_whenGetAgeInMonthsIsCalled_thenReturnsMonthsBetweenBirthDateAndToday() {
        LocalDate birthDate = LocalDate.of(1990, 1, 1);
        Profile profile = Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                birthDate, 60.0, 165.0, ActivityLevel.MODERATE, null,
                Set.of(), LocalDateTime.now(), LocalDateTime.now());

        int ageInMonths = profile.getAgeInMonths();

        assertThat(ageInMonths).isEqualTo((int) ChronoUnit.MONTHS.between(birthDate, LocalDate.now()));
    }
}
