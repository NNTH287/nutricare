package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;

import java.util.Map;

public final class CalorieFormulaRegistry {
    private static final CalorieFormula DEFAULT_FORMULA = new LinearCoefficientFormula();
    private static final Map<GroupType, CalorieFormula> FORMULAS_BY_GROUP_TYPE =
            Map.of(GroupType.INFANT, new PerKilogramFormula());

    private CalorieFormulaRegistry() {}

    public static CalorieFormula resolve(GroupType groupType) {
        return FORMULAS_BY_GROUP_TYPE.getOrDefault(groupType, DEFAULT_FORMULA);
    }
}
