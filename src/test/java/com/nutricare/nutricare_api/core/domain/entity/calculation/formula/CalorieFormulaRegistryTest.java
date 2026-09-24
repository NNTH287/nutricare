package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalorieFormulaRegistryTest {

    @Test
    void givenInfantGroupType_whenResolveIsCalled_thenReturnsPerKilogramFormula() {
        assertThat(CalorieFormulaRegistry.resolve(GroupType.INFANT)).isInstanceOf(PerKilogramFormula.class);
    }

    @Test
    void givenAdultGroupType_whenResolveIsCalled_thenReturnsLinearCoefficientFormula() {
        assertThat(CalorieFormulaRegistry.resolve(GroupType.ADULT)).isInstanceOf(LinearCoefficientFormula.class);
    }

    @Test
    void givenEveryNonInfantGroupType_whenResolveIsCalled_thenReturnsLinearCoefficientFormula() {
        for (GroupType groupType : GroupType.values()) {
            if (groupType == GroupType.INFANT) {
                continue;
            }
            assertThat(CalorieFormulaRegistry.resolve(groupType)).isInstanceOf(LinearCoefficientFormula.class);
        }
    }
}
