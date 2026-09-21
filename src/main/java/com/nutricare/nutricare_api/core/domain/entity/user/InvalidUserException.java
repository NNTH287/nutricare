package com.nutricare.nutricare_api.core.domain.entity.user;

public class InvalidUserException extends RuntimeException {
    public InvalidUserException(String message) {
        super(message);
    }
}
