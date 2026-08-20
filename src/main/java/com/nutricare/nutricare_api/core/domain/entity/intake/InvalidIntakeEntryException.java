package com.nutricare.nutricare_api.core.domain.entity.intake;

public class InvalidIntakeEntryException extends RuntimeException {
    public InvalidIntakeEntryException(String message) {
        super(message);
    }
}
