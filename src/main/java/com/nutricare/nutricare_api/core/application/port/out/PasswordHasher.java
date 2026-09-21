package com.nutricare.nutricare_api.core.application.port.out;

public interface PasswordHasher {
    String hash(String rawPassword);

    boolean matches(String rawPassword, String hash);
}
