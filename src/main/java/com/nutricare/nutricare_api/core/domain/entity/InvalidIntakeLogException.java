package com.nutricare.nutricare_api.core.domain.entity;

public class InvalidIntakeLogException extends RuntimeException {
    public InvalidIntakeLogException(String message) {
        super(message);
    }
}
