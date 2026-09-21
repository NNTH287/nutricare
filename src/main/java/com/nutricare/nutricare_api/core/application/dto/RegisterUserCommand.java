package com.nutricare.nutricare_api.core.application.dto;

public record RegisterUserCommand(String email, String rawPassword) {
}
