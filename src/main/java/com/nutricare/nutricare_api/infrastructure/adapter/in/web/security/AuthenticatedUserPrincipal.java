package com.nutricare.nutricare_api.infrastructure.adapter.in.web.security;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

public record AuthenticatedUserPrincipal(Integer userId, String email, Role role) {
}
