package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

public record AuthResponse(String token, Integer userId, String email, Role role) {
}
