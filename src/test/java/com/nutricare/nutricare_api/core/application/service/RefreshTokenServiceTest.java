package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.application.port.out.UserRepository;
import com.nutricare.nutricare_api.core.application.port.out.VerifiedRefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidRefreshTokenException;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshTokenReuseDetectedException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.core.domain.entity.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TokenIssuer tokenIssuer;

    private RefreshTokenService service;

    private static User user() {
        return User.reconstitute(1, "user@example.com", "hashed-password", Role.USER,
                LocalDateTime.now(), LocalDateTime.now());
    }

    @BeforeEach
    void setUp() {
        service = new RefreshTokenService(refreshTokenRepository, userRepository, tokenIssuer);
    }

    @Test
    void givenTokenThatFailsSignatureOrTypeVerification_whenRefreshIsCalled_thenThrowsInvalidRefreshTokenException() {
        when(tokenIssuer.verifyRefreshToken("bad-token")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh("bad-token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void givenVerifiedTokenWithUnknownJti_whenRefreshIsCalled_thenThrowsInvalidRefreshTokenException() {
        UUID jti = UUID.randomUUID();
        when(tokenIssuer.verifyRefreshToken("token")).thenReturn(Optional.of(new VerifiedRefreshToken(1, jti)));
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.refresh("token"))
                .isInstanceOf(InvalidRefreshTokenException.class);
    }

    @Test
    void givenExpiredStoredToken_whenRefreshIsCalled_thenThrowsInvalidRefreshTokenExceptionAndDoesNotRotate() {
        UUID jti = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken expired = RefreshToken.issue(jti, 1, now.minusDays(10), now.minusDays(1));
        when(tokenIssuer.verifyRefreshToken("token")).thenReturn(Optional.of(new VerifiedRefreshToken(1, jti)));
        when(refreshTokenRepository.findByJti(jti)).thenReturn(Optional.of(expired));

        assertThatThrownBy(() -> service.refresh("token"))
                .isInstanceOf(InvalidRefreshTokenException.class);

        verify(refreshTokenRepository, never()).save(any());
        verify(tokenIssuer, never()).issueAccessToken(any(), any(), any());
    }

    @Test
    void givenRevokedStoredToken_whenRefreshIsCalled_thenThrowsReuseExceptionAndRevokesEveryActiveTokenForUser() {
        UUID reusedJti = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken revoked = RefreshToken.issue(reusedJti, 1, now.minusDays(2), now.plusDays(5));
        revoked.revoke(now.minusHours(1), UUID.randomUUID());

        RefreshToken otherActive1 = RefreshToken.issue(UUID.randomUUID(), 1, now.minusDays(1), now.plusDays(6));
        RefreshToken otherActive2 = RefreshToken.issue(UUID.randomUUID(), 1, now.minusDays(1), now.plusDays(6));

        when(tokenIssuer.verifyRefreshToken("stolen-token"))
                .thenReturn(Optional.of(new VerifiedRefreshToken(1, reusedJti)));
        when(refreshTokenRepository.findByJti(reusedJti)).thenReturn(Optional.of(revoked));
        when(refreshTokenRepository.findActiveByUserId(1)).thenReturn(List.of(otherActive1, otherActive2));

        assertThatThrownBy(() -> service.refresh("stolen-token"))
                .isInstanceOf(RefreshTokenReuseDetectedException.class);

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues()).allSatisfy(token -> assertThat(token.isRevoked()).isTrue());
        verify(tokenIssuer, never()).issueAccessToken(any(), any(), any());
    }

    @Test
    void givenValidStoredToken_whenRefreshIsCalled_thenRevokesOldTokenIssuesNewPairAndPersistsIt() {
        UUID oldJti = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken stored = RefreshToken.issue(oldJti, 1, now.minusDays(1), now.plusDays(6));

        when(tokenIssuer.verifyRefreshToken("token")).thenReturn(Optional.of(new VerifiedRefreshToken(1, oldJti)));
        when(refreshTokenRepository.findByJti(oldJti)).thenReturn(Optional.of(stored));
        when(userRepository.findById(1)).thenReturn(Optional.of(user()));
        when(tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER)).thenReturn("new-access-jwt");
        when(tokenIssuer.issueRefreshToken(eq(1), any(UUID.class)))
                .thenReturn(new IssuedRefreshToken("new-refresh-jwt", now.plusDays(7)));

        AuthenticatedUserResult result = service.refresh("token");

        assertThat(result.accessToken()).isEqualTo("new-access-jwt");
        assertThat(result.refreshToken()).isEqualTo("new-refresh-jwt");
        assertThat(stored.isRevoked()).isTrue();

        ArgumentCaptor<RefreshToken> captor = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues().get(0)).isSameAs(stored);
        assertThat(captor.getAllValues().get(1).getUserId()).isEqualTo(1);
        assertThat(captor.getAllValues().get(1).isRevoked()).isFalse();
        assertThat(stored.getReplacedByJti()).isEqualTo(captor.getAllValues().get(1).getJti());
    }
}
