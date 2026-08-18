package com.nutricare.nutricare_api.core.domain.entity;

public class InvalidProfileStateException extends RuntimeException {
    public InvalidProfileStateException(String message) {
        super(message);
    }
}
