package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public class LinearCoefficientFormula implements CalorieFormula {
    @Override
    public double dailyCalories(Profile profile, EnergyCoefficient coefficient) {
        double bmr = coefficient.getWeightCoefficient() * profile.getWeightKg()
                + coefficient.getHeightCoefficient() * profile.getHeightCm()
                + coefficient.getIntercept();
        return bmr * profile.getActivityLevel().getMultiplier();
    }
}
