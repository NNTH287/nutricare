package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;

public interface TokenIssuer {
    String issueAccessToken(Integer userId, String email, Role role);
}
