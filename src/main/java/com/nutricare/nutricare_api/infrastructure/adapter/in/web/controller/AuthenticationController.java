package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.AuthResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.LoginRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final RegisterUserUseCase registerUseCase;
    private final AuthenticateUserUseCase authenticateUseCase;
    private final AuthWebMapper mapper;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest req) {
        var result = registerUseCase.register(mapper.toRegisterCommand(req));
        return ApiResponse.created(URI.create("/api/auth/login"), mapper.toResponse(result));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest req) {
        var result = authenticateUseCase.login(req.email(), req.password());
        return ApiResponse.ok(mapper.toResponse(result));
    }
}
