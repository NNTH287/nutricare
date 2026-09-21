package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;

public interface CalculateNutrientUseCase {
    CalculateNutrientResult calculateNutrientResult(Integer profileId, Integer nutrientStandardId, Integer authenticatedUserId);

    CalculateNutrientResult saveCalculationResult(Integer profileId, Integer nutrientStandardId, Integer authenticatedUserId);
}
