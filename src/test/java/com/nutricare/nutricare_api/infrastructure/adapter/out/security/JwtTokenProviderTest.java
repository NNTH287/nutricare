package com.nutricare.nutricare_api.infrastructure.adapter.out.security;

import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private static final String SECRET = "test-secret-not-for-production-use-only-in-tests";

    private final JwtTokenProvider provider = new JwtTokenProvider(new JwtProperties(SECRET, 60));

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
                new JwtProperties("a-completely-different-signing-secret-value", 60));
        String foreignToken = other.issueAccessToken(7, "user@example.com", Role.ADMIN);

        assertThat(provider.validateAndParse(foreignToken, TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenExpiredToken_whenValidated_thenIsRejected() throws InterruptedException {
        JwtTokenProvider instantlyExpiring = new JwtTokenProvider(new JwtProperties(SECRET, 0));
        String token = instantlyExpiring.issueAccessToken(7, "user@example.com", Role.USER);
        Thread.sleep(1_000);

        assertThat(instantlyExpiring.validateAndParse(token, TokenType.ACCESS)).isEmpty();
    }

    @Test
    void givenMalformedToken_whenValidated_thenIsRejected() {
        assertThat(provider.validateAndParse("not-a-jwt", TokenType.ACCESS)).isEmpty();
    }
}
