package com.nutricare.nutricare_api.core.application.port.out;

import java.util.UUID;

public record VerifiedRefreshToken(Integer userId, UUID jti) {
}
