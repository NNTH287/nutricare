package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.dto.RegisterUserCommand;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.AuthResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.RegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AuthWebMapper {
    @Mapping(target = "rawPassword", source = "password")
    RegisterUserCommand toRegisterCommand(RegisterRequest request);

    AuthResponse toResponse(AuthenticatedUserResult result);
}
