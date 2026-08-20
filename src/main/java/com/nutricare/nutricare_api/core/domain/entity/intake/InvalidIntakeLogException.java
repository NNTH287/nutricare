package com.nutricare.nutricare_api.core.domain.entity.intake;

public class InvalidIntakeLogException extends RuntimeException {
    public InvalidIntakeLogException(String message) {
        super(message);
    }
}
