package com.nutricare.nutricare_api.infrastructure.adapter.out.security;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

public record TokenClaims(Integer userId, String email, Role role) {
}
