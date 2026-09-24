package com.nutricare.nutricare_api.infrastructure.adapter.out.security;

import com.nutricare.nutricare_api.core.application.port.out.IssuedRefreshToken;
import com.nutricare.nutricare_api.core.application.port.out.VerifiedRefreshToken;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-not-for-production-use-only-in-tests";

    private final JwtTokenProvider provider = new JwtTokenProvider(new JwtProperties(SECRET, 60, 7));

    @Test
    void givenAccessToken_whenValidatedAsAccess_thenReturnsClaims() {
        String token = provider.issueAccessToken(7, "user@example.com", Role.USER);

        Optional<TokenClaims> claims = provider.validateAndParse(token, TokenType.ACCESS);

        assertThat(claims).isPresent();
        assertThat(claims.get().userId()).isEqualTo(7);
        assertThat(claims.get().email()).isEqualTo("user@example.com");
        assertThat(claims.get().role()).isEqualTo(Role.USER);
        assertThat(claims.get().type()).isEqualTo(TokenType.ACCESS);
    }

    @Test
    void givenAccessToken_whenValidatedAsRefresh_thenIsRejected() {
        String token = provider.issueAccessToken(7, "user@example.com", Role.USER);

        assertThat(provider.validateAndParse(token, TokenType.REFRESH)).isEmpty();
    }

    @Test
    void givenTokenSignedWithAnotherKey_whenValidated_thenIsRejected() {
        JwtTokenProvider other = new JwtTokenProvider(
                new JwtProperties("a-completely-different-signing-secret-value", 60, 7));
        String foreignToken = other.issueAccessToken(7, "user@example.com", Role.ADMIN);

        assertThat(provider.validateAndParse(foreignToken, TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenExpiredToken_whenValidated_thenIsRejected() throws InterruptedException {
        JwtTokenProvider instantlyExpiring = new JwtTokenProvider(new JwtProperties(SECRET, 0, 7));
        String token = instantlyExpiring.issueAccessToken(7, "user@example.com", Role.USER);
        Thread.sleep(1_000);

        assertThat(instantlyExpiring.validateAndParse(token, TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenMalformedToken_whenValidated_thenIsRejected() {
        assertThat(provider.validateAndParse("not-a-jwt", TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenRefreshToken_whenValidatedAsRefresh_thenReturnsClaimsWithJti() {
        UUID jti = UUID.randomUUID();
        IssuedRefreshToken issued = provider.issueRefreshToken(7, jti);

        Optional<TokenClaims> claims = provider.validateAndParse(issued.token(), TokenType.REFRESH);

        assertThat(claims).isPresent();
        assertThat(claims.get().userId()).isEqualTo(7);
        assertThat(claims.get().type()).isEqualTo(TokenType.REFRESH);
        assertThat(claims.get().jti()).isEqualTo(jti);
    }

    @Test
    void givenRefreshToken_whenValidatedAsAccess_thenIsRejected() {
        IssuedRefreshToken issued = provider.issueRefreshToken(7, UUID.randomUUID());

        assertThat(provider.validateAndParse(issued.token(), TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenRefreshToken_whenVerifyRefreshTokenIsCalled_thenReturnsUserIdAndJti() {
        UUID jti = UUID.randomUUID();
        IssuedRefreshToken issued = provider.issueRefreshToken(7, jti);

        Optional<VerifiedRefreshToken> verified = provider.verifyRefreshToken(issued.token());

        assertThat(verified).contains(new VerifiedRefreshToken(7, jti));
    }

    @Test
    void givenAccessToken_whenVerifyRefreshTokenIsCalled_thenReturnsEmpty() {
        String token = provider.issueAccessToken(7, "user@example.com", Role.USER);

        assertThat(provider.verifyRefreshToken(token)).isEmpty();
    }
}
