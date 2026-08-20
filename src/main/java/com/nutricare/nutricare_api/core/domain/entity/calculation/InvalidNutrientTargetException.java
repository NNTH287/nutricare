package com.nutricare.nutricare_api.core.domain.entity.calculation;

public class InvalidNutrientTargetException extends RuntimeException {
    public InvalidNutrientTargetException(String message) {
        super(message);
    }
}
