package com.nutricare.nutricare_api.infrastructure.adapter.out.security;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

import java.util.UUID;

public record TokenClaims(Integer userId, String email, Role role, TokenType type, UUID jti) {
}
