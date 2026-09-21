package com.nutricare.nutricare_api.core.domain.entity.user;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String message) {
        super(message);
    }
}
