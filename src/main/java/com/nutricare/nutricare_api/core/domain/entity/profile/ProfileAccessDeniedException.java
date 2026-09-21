package com.nutricare.nutricare_api.core.domain.entity.profile;

public class ProfileAccessDeniedException extends RuntimeException {
    public ProfileAccessDeniedException(String message) {
        super(message);
    }
}
