package com.nutricare.nutricare_api.core.domain.entity.user;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String message) {
        super(message);
    }
}
