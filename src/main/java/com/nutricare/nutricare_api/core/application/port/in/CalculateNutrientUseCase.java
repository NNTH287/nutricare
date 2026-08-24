package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;

public interface CalculateNutrientUseCase {
    CalculateNutrientResult calculateNutrientResult (CalculateNutrientCommand command);
}
