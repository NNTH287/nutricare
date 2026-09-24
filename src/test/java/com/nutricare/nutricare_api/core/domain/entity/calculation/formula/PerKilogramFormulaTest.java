package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.profile.ActivityLevel;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

class PerKilogramFormulaTest {

    @Test
    void givenWeightCoefficientAndProfileWeight_whenDailyCaloriesIsCalled_thenIgnoresActivityMultiplier() {
        LocalDateTime now = LocalDateTime.now();
        Profile profile = Profile.reconstitute(1, 10, "Baby", GroupType.INFANT, SexType.MALE,
                LocalDate.now().minusMonths(4), 6.0, 60.0, ActivityLevel.VERY_ACTIVE, null, Set.of(), now, now);
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.INFANT, SexType.MALE, 0, 12, 110.0, 0.0, 0.0);

        double result = new PerKilogramFormula().dailyCalories(profile, coefficient);

        assertThat(result).isCloseTo(110.0 * 6.0, within(0.001));
    }
}
