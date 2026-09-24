package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;

public interface RefreshTokenUseCase {
    AuthenticatedUserResult refresh(String refreshToken);
}
