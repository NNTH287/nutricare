package com.nutricare.nutricare_api.core.domain.entity.menu;

public class InvalidMenuItemException extends RuntimeException {
    public InvalidMenuItemException(String message) {
        super(message);
    }
}
