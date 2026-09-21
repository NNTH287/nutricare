package com.nutricare.nutricare_api.core.application.dto;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

public record AuthenticatedUserResult(String token, Integer userId, String email, Role role) {
}
