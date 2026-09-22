package com.nutricare.nutricare_api.core.domain.entity.calculation;

import com.nutricare.nutricare_api.core.domain.entity.profile.ActivityLevel;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.InvalidTrimesterException;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class EnergyCoefficientTest {

    private static Profile adultProfile(int ageYears, SexType sexType, double weightKg, double heightCm, ActivityLevel activityLevel) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, sexType,
                LocalDate.now().minusYears(ageYears), weightKg, heightCm, activityLevel, null, Set.of(), now, now);
    }

    @Test
    void givenPregnantGroupType_whenCreateIsCalled_thenThrowsInvalidTrimesterException() {
        assertThatThrownBy(() -> EnergyCoefficient.create(1, GroupType.PREGNANT, SexType.FEMALE, 0, 1200, 10, 6.25, 5))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenInvertedAgeRange_whenCreateIsCalled_thenThrowsInvalidEnergyCoefficientException() {
        assertThatThrownBy(() -> EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 300, 200, 10, 6.25, 5))
                .isInstanceOf(InvalidEnergyCoefficientException.class);
    }

    @Test
    void givenNullStandardId_whenCreateIsCalled_thenThrowsInvalidEnergyCoefficientException() {
        assertThatThrownBy(() -> EnergyCoefficient.create(null, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10, 6.25, 5))
                .isInstanceOf(InvalidEnergyCoefficientException.class);
    }

    @Test
    void givenProfileMatchingGroupSexAgeAndTrimester_whenMatchesIsCalled_thenReturnsTrue() {
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10, 6.25, 5);
        Profile profile = adultProfile(30, SexType.FEMALE, 70.0, 175.0, ActivityLevel.SEDENTARY);

        assertThat(coefficient.matches(profile)).isTrue();
    }

    @Test
    void givenProfileWithDifferentGroupType_whenMatchesIsCalled_thenReturnsFalse() {
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10, 6.25, 5);
        Profile child = Profile.reconstitute(1, 10, "Kid", GroupType.CHILD, SexType.FEMALE,
                LocalDate.now().minusYears(5), 20.0, 110.0, ActivityLevel.ACTIVE, null, Set.of(),
                LocalDateTime.now(), LocalDateTime.now());

        assertThat(coefficient.matches(child)).isFalse();
    }

    @Test
    void givenProfileOutsideAgeRange_whenMatchesIsCalled_thenReturnsFalse() {
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 100, 10, 6.25, 5);
        Profile profile = adultProfile(30, SexType.FEMALE, 70.0, 175.0, ActivityLevel.SEDENTARY);

        assertThat(coefficient.matches(profile)).isFalse();
    }

    @Test
    void givenWeightHeightAndInterceptCoefficients_whenResolveTargetIsCalled_thenReturnsBmrTimesActivityMultiplier() {
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);
        Profile profile = adultProfile(30, SexType.FEMALE, 70.0, 175.0, ActivityLevel.SEDENTARY);

        double target = coefficient.resolveTarget(profile);

        double expectedBmr = 10.0 * 70.0 + 6.25 * 175.0 + 5.0;
        assertThat(target).isCloseTo(expectedBmr * ActivityLevel.SEDENTARY.getMultiplier(), within(0.001));
    }

    @Test
    void givenPregnantGroupTypeAndTrimester_whenCreateForPregnantIsCalled_thenCoefficientIsCreated() {
        EnergyCoefficient coefficient = EnergyCoefficient.createForPregnant(1, SexType.FEMALE, 200, 500, 2, 10, 6.25, 5);

        assertThat(coefficient.getGroupType()).isEqualTo(GroupType.PREGNANT);
        assertThat(coefficient.getTrimester()).isEqualTo(2);
    }
}
