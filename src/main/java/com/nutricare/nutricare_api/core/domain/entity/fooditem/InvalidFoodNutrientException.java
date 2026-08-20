package com.nutricare.nutricare_api.core.domain.entity.fooditem;

public class InvalidFoodNutrientException extends RuntimeException {
    public InvalidFoodNutrientException(String message) {
        super(message);
    }
}
