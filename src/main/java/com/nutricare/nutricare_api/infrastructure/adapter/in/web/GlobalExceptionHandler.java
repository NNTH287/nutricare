package com.nutricare.nutricare_api.infrastructure.adapter.in.web;

import com.nutricare.nutricare_api.core.domain.entity.profile.ProfileAccessDeniedException;
import com.nutricare.nutricare_api.core.domain.entity.user.DuplicateEmailException;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidRefreshTokenException;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshTokenReuseDetectedException;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(ProfileAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleProfileAccessDenied(ProfileAccessDeniedException ex) {
        return ApiResponse.error(HttpStatus.FORBIDDEN, ex.getMessage());
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateEmail(DuplicateEmailException ex) {
        return ApiResponse.error(HttpStatus.CONFLICT, ex.getMessage());
    }

    @ExceptionHandler(InvalidRefreshTokenException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidRefreshToken(InvalidRefreshTokenException ex) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, ex.getMessage());
    }

    @ExceptionHandler(RefreshTokenReuseDetectedException.class)
    public ResponseEntity<ApiResponse<Void>> handleRefreshTokenReuseDetected(RefreshTokenReuseDetectedException ex) {
        return ApiResponse.error(HttpStatus.UNAUTHORIZED, "Refresh token is invalid or expired");
    }
}
