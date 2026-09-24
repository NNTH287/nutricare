package com.nutricare.nutricare_api.core.application.port.out;

import java.time.LocalDateTime;

public record IssuedRefreshToken(String token, LocalDateTime expiresAt) {
}
