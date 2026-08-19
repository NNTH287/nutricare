package com.nutricare.nutricare_api.core.domain.entity;

public class InvalidMenuException extends RuntimeException {
    public InvalidMenuException(String message) {
        super(message);
    }
}
