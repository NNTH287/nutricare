package com.nutricare.nutricare_api.core.domain.entity.fooditem;

public record NutrientAmount(Integer nutrientId, Double amountPer100g) {
    public NutrientAmount {
        if (nutrientId == null) {
            throw new InvalidFoodNutrientException("nutrientId is required");
        }
        if (amountPer100g == null || amountPer100g <= 0) {
            throw new InvalidFoodNutrientException("amountPer100g must be positive");
        }
    }
}
