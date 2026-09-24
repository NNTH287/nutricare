package com.nutricare.nutricare_api.core.domain.entity.calculation.formula;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public interface CalorieFormula {
    double dailyCalories(Profile profile, EnergyCoefficient coefficient);
}
