package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.dto.RegisterUserCommand;

public interface RegisterUserUseCase {
    AuthenticatedUserResult register(RegisterUserCommand command);
}
