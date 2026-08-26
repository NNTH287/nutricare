package com.nutricare.nutricare_api.core.domain.entity.fooditem;

public class InvalidNutrientException extends RuntimeException {
    public InvalidNutrientException(String message) {
        super(message);
    }
}
