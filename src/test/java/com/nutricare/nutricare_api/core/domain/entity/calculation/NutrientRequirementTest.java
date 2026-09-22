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

class NutrientRequirementTest {

    private static Profile adultProfile(int ageYears) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.now().minusYears(ageYears), 60.0, 165.0, ActivityLevel.MODERATE, null, Set.of(), now, now);
    }

    @Test
    void givenPregnantGroupType_whenCreateIsCalled_thenThrowsInvalidTrimesterException() {
        assertThatThrownBy(() -> NutrientRequirement.create(1, GroupType.PREGNANT, 5, 0, 1200, 10.0, 20.0))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenProfileMatchingGroupAndAgeRange_whenMatchesIsCalled_thenReturnsTrue() {
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, 10.0, 20.0);

        assertThat(requirement.matches(adultProfile(30))).isTrue();
    }

    @Test
    void givenProfileOutsideAgeRange_whenMatchesIsCalled_thenReturnsFalse() {
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 100, 10.0, 20.0);

        assertThat(requirement.matches(adultProfile(30))).isFalse();
    }

    @Test
    void givenProfileWithDifferentGroupType_whenMatchesIsCalled_thenReturnsFalse() {
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ELDERLY, 5, 0, 1200, 10.0, 20.0);

        assertThat(requirement.matches(adultProfile(30))).isFalse();
    }

    @Test
    void givenNonPregnantRequirement_whenSetTrimesterIsCalled_thenThrowsInvalidTrimesterException() {
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, 10.0, 20.0);

        assertThatThrownBy(() -> requirement.setTrimester(1))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenPregnantRequirement_whenSetTrimesterWithValidValue_thenTrimesterIsUpdated() {
        NutrientRequirement requirement = NutrientRequirement.createForPregnant(1, 5, 0, 1200, 1, 10.0, 20.0);

        requirement.setTrimester(2);

        assertThat(requirement.getTrimester()).isEqualTo(2);
    }
}
