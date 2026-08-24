package com.nutricare.nutricare_api.core.domain.entity.calculation;

public class InvalidEnergyCoefficientException extends RuntimeException {
    public InvalidEnergyCoefficientException(String message) {
        super(message);
    }
}
