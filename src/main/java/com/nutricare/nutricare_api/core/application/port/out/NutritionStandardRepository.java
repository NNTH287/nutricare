package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;

public interface NutritionStandardRepository {
    NutritionStandard getById(Integer integer);
}
