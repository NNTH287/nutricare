package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public class PerKilogramFormula implements CalorieFormula {
    @Override
    public double dailyCalories(Profile profile, EnergyCoefficient coefficient) {
        return coefficient.getWeightCoefficient() * profile.getWeightKg();
    }
}
