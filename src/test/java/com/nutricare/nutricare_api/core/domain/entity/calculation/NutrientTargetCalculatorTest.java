package com.nutricare.nutricare_api.core.domain.entity.calculation;

import com.nutricare.nutricare_api.core.domain.entity.profile.ActivityLevel;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

class NutrientTargetCalculatorTest {

    private static Profile adultProfile(double weightKg, double heightCm, ActivityLevel activityLevel) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.now().minusYears(30), weightKg, heightCm, activityLevel, null, Set.of(), now, now);
    }

    @Test
    void givenMatchingRequirementWithRecommendedValue_whenCalculateIsCalled_thenReturnsResolvedTarget() {
        Profile profile = adultProfile(60.0, 165.0, ActivityLevel.MODERATE);
        NutrientRequirement matching = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, 10.0, 20.0);

        NutrientCalculation calculation = NutrientTargetCalculator.calculate(profile, List.of(matching));

        assertThat(calculation.getResolvedTargets()).hasSize(1);
        assertThat(calculation.getResolvedTargets().iterator().next().getNutrientId()).isEqualTo(5);
        assertThat(calculation.getUnresolvedTargets()).isEmpty();
    }

    @Test
    void givenMatchingRequirementWithoutRecommendedValue_whenCalculateIsCalled_thenNutrientIdIsUnresolved() {
        Profile profile = adultProfile(60.0, 165.0, ActivityLevel.MODERATE);
        NutrientRequirement matching = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, null, null);

        NutrientCalculation calculation = NutrientTargetCalculator.calculate(profile, List.of(matching));

        assertThat(calculation.getResolvedTargets()).isEmpty();
        assertThat(calculation.getUnresolvedTargets()).containsExactly(5);
    }

    @Test
    void givenNonMatchingRequirement_whenCalculateIsCalled_thenItIsIgnored() {
        Profile profile = adultProfile(60.0, 165.0, ActivityLevel.MODERATE);
        NutrientRequirement nonMatching = NutrientRequirement.create(1, GroupType.ELDERLY, 5, 0, 1200, 10.0, 20.0);

        NutrientCalculation calculation = NutrientTargetCalculator.calculate(profile, List.of(nonMatching));

        assertThat(calculation.getResolvedTargets()).isEmpty();
        assertThat(calculation.getUnresolvedTargets()).isEmpty();
    }

    @Test
    void givenMatchingCoefficient_whenResolveCalorieTargetIsCalled_thenReturnsCoefficientResolvedTarget() {
        Profile profile = adultProfile(70.0, 175.0, ActivityLevel.SEDENTARY);
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);

        double calorieTarget = NutrientTargetCalculator.resolveCalorieTarget(profile, 1, List.of(coefficient));

        double expected = (10.0 * 70.0 + 6.25 * 175.0 + 5.0) * ActivityLevel.SEDENTARY.getMultiplier();
        assertThat(calorieTarget).isCloseTo(expected, within(0.001));
    }

    @Test
    void givenNoMatchingCoefficient_whenResolveCalorieTargetIsCalled_thenThrowsUnresolvableCalorieTargetException() {
        Profile profile = adultProfile(70.0, 175.0, ActivityLevel.SEDENTARY);
        EnergyCoefficient nonMatching = EnergyCoefficient.create(1, GroupType.ELDERLY, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);

        assertThatThrownBy(() -> NutrientTargetCalculator.resolveCalorieTarget(profile, 1, List.of(nonMatching)))
                .isInstanceOf(UnresolvableCalorieTargetException.class);
    }
}
