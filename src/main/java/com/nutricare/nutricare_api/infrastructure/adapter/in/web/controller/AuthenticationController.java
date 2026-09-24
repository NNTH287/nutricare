package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RefreshTokenUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.AuthResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.LoginRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.AuthWebMapper;
import com.nutricare.nutricare_api.infrastructure.adapter.out.security.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.Duration;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private static final String REFRESH_TOKEN_COOKIE = "refresh_token";

    private final RegisterUserUseCase registerUseCase;
    private final AuthenticateUserUseCase authenticateUseCase;
    private final RefreshTokenUseCase refreshTokenUseCase;
    private final AuthWebMapper mapper;
    private final JwtProperties jwtProperties;

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> register(@RequestBody RegisterRequest req) {
        var result = registerUseCase.register(mapper.toRegisterCommand(req));
        return ApiResponse.created(URI.create("/api/auth/login"), mapper.toResponse(result), refreshTokenCookie(result.refreshToken()));
    }

    @PostMapping("/login")
    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> login(@RequestBody LoginRequest req) {
        var result = authenticateUseCase.login(req.email(), req.password());
        return ApiResponse.ok(mapper.toResponse(result), refreshTokenCookie(result.refreshToken()));
    }

    @PostMapping("/refresh")
    @Transactional
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@CookieValue(REFRESH_TOKEN_COOKIE) String refreshToken) {
        var result = refreshTokenUseCase.refresh(refreshToken);
        return ApiResponse.ok(mapper.toResponse(result), refreshTokenCookie(result.refreshToken()));
    }

    private ResponseCookie refreshTokenCookie(String token) {
        return ResponseCookie.from(REFRESH_TOKEN_COOKIE, token)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/api/auth/refresh")
                .maxAge(Duration.ofDays(jwtProperties.refreshTokenExpirationDays()))
                .build();
    }
}
