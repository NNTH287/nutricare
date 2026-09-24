package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PurgeExpiredRefreshTokensServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void whenPurgeExpiredTokensIsCalled_thenDeletesTokensExpiredBeforeNow() {
        PurgeExpiredRefreshTokensService service = new PurgeExpiredRefreshTokensService(refreshTokenRepository);
        LocalDateTime before = LocalDateTime.now();

        service.purgeExpiredTokens();

        LocalDateTime after = LocalDateTime.now();
        ArgumentCaptor<LocalDateTime> captor = ArgumentCaptor.forClass(LocalDateTime.class);
        verify(refreshTokenRepository).deleteExpiredBefore(captor.capture());
        assertThat(captor.getValue()).isBetween(before, after);
    }
}
