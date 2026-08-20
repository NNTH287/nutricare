package com.nutricare.nutricare_api.core.domain.entity.calculation;

public class InvalidCalculationResultException extends RuntimeException {
    public InvalidCalculationResultException(String message) {
        super(message);
    }
}
