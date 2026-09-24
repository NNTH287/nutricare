package com.nutricare.nutricare_api.infrastructure.adapter.in.scheduler;

import com.nutricare.nutricare_api.core.application.port.in.PurgeExpiredRefreshTokensUseCase;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RefreshTokenPurgeJobTest {

    @Test
    void whenPurgeExpiredRefreshTokensIsCalled_thenDelegatesToUseCase() {
        PurgeExpiredRefreshTokensUseCase useCase = mock(PurgeExpiredRefreshTokensUseCase.class);
        RefreshTokenPurgeJob job = new RefreshTokenPurgeJob(useCase);

        job.purgeExpiredRefreshTokens();

        verify(useCase).purgeExpiredTokens();
    }
}
