package com.nutricare.nutricare_api.core.domain.entity;

public class InvalidMenuItemException extends RuntimeException {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
