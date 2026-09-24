package com.nutricare.nutricare_api.core.domain.entity.user;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RefreshTokenTest {

    @Test
    void givenNullJti_whenIssueIsCalled_thenThrowsInvalidRefreshTokenException() {
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> RefreshToken.issue(null, 1, now, now.plusDays(7)))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void givenExpiresAtBeforeIssuedAt_whenIssueIsCalled_thenThrowsInvalidRefreshTokenException() {
        LocalDateTime now = LocalDateTime.now();

        assertThatThrownBy(() -> RefreshToken.issue(UUID.randomUUID(), 1, now, now.minusDays(1)))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void givenFreshToken_whenVerifyUsableIsCalled_thenDoesNotThrow() {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), 1, now, now.plusDays(7));

        token.verifyUsable(now);
    }

    @Test
    void givenExpiredToken_whenVerifyUsableIsCalled_thenThrowsInvalidRefreshTokenException() {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), 1, now.minusDays(10), now.minusDays(3));

        assertThatThrownBy(() -> token.verifyUsable(now))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void givenRevokedToken_whenVerifyUsableIsCalled_thenThrowsRefreshTokenReuseDetectedException() {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), 1, now, now.plusDays(7));
        token.revoke(now, UUID.randomUUID());

        assertThatThrownBy(() -> token.verifyUsable(now))
                .isInstanceOf(RefreshTokenReuseDetectedException.class);
    }

    @Test
    void givenFreshToken_whenRevokeIsCalled_thenIsRevokedAndCarriesReplacement() {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), 1, now, now.plusDays(7));
        UUID replacement = UUID.randomUUID();

        token.revoke(now, replacement);

        assertThat(token.isRevoked()).isTrue();
        assertThat(token.getRevokedAt()).isEqualTo(now);
        assertThat(token.getReplacedByJti()).isEqualTo(replacement);
    }

    @Test
    void givenAlreadyRevokedToken_whenRevokeIsCalledAgain_thenFirstRevocationIsPreserved() {
        LocalDateTime firstRevocation = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(UUID.randomUUID(), 1, firstRevocation.minusDays(1), firstRevocation.plusDays(7));
        UUID firstReplacement = UUID.randomUUID();
        token.revoke(firstRevocation, firstReplacement);

        token.revoke(firstRevocation.plusMinutes(5), UUID.randomUUID());

        assertThat(token.getRevokedAt()).isEqualTo(firstRevocation);
        assertThat(token.getReplacedByJti()).isEqualTo(firstReplacement);
    }
}
