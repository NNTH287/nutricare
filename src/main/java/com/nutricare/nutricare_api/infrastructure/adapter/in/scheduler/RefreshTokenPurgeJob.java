package com.nutricare.nutricare_api.infrastructure.adapter.in.scheduler;

import com.nutricare.nutricare_api.core.application.port.in.PurgeExpiredRefreshTokensUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "app.scheduling", name = "enabled", havingValue = "true", matchIfMissing = true)
public class RefreshTokenPurgeJob {
    private final PurgeExpiredRefreshTokensUseCase useCase;

    @Scheduled(cron = "0 0 3 * * *")
    public void purgeExpiredRefreshTokens() {
        useCase.purgeExpiredTokens();
    }
}
