package com.nutricare.nutricare_api.core.domain.entity.menu;

public class InvalidMenuException extends RuntimeException {
    public InvalidMenuException(String message) {
        super(message);
    }
}
