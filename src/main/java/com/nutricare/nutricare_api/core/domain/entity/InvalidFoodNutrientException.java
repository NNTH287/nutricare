package com.nutricare.nutricare_api.core.domain.entity;

public class InvalidFoodNutrientException extends RuntimeException {
    public InvalidFoodNutrientException(String message) {
        super(message);
    }
}
