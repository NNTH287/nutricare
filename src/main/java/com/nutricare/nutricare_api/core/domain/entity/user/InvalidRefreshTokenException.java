package com.nutricare.nutricare_api.core.domain.entity.user;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String message) {
        super(message);
    }
}
