package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;

public interface AuthenticateUserUseCase {
    AuthenticatedUserResult login(String email, String rawPassword);
}
