package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

public interface NutrientRepository {
    Nutrient getById(Integer nutrientId);
}
