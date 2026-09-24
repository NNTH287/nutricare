package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.RefreshTokenPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RefreshTokenRepositoryAdapterTest {

    @Autowired
    private RefreshTokenJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private RefreshTokenRepositoryAdapter adapter;
    private int userId;

    @BeforeEach
    void setUp() {
        adapter = new RefreshTokenRepositoryAdapter(jpaRepository, new RefreshTokenPersistenceMapper() {});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(
                "insert into users (email, password_hash, role, created_at, updated_at) "
                        + "values ('owner@example.com', 'hash', 'USER', now(), now())",
                Statement.RETURN_GENERATED_KEYS), keyHolder);
        userId = keyHolder.getKey().intValue();
    }

    @Test
    void givenNewRefreshToken_whenSaveIsCalled_thenItIsPersistedAndFindableByJti() {
        UUID jti = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(jti, userId, now, now.plusDays(7));

        adapter.save(token);
        Optional<RefreshToken> found = adapter.findByJti(jti);

        assertThat(found).isPresent();
        assertThat(found.get().getUserId()).isEqualTo(userId);
        assertThat(found.get().isRevoked()).isFalse();
    }

    @Test
    void givenNoRefreshTokenWithGivenJti_whenFindByJtiIsCalled_thenReturnsEmpty() {
        assertThat(adapter.findByJti(UUID.randomUUID())).isEmpty();
    }

    @Test
    void givenRevokedAndReplacedToken_whenSavedAndReloaded_thenRevocationFieldsPersist() {
        UUID jti = UUID.randomUUID();
        UUID replacement = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();
        RefreshToken token = RefreshToken.issue(jti, userId, now, now.plusDays(7));
        token.revoke(now, replacement);

        adapter.save(token);
        Optional<RefreshToken> found = adapter.findByJti(jti);

        assertThat(found).isPresent();
        assertThat(found.get().isRevoked()).isTrue();
        assertThat(found.get().getReplacedByJti()).isEqualTo(replacement);
    }

    @Test
    void givenMixOfActiveAndRevokedTokensForUser_whenFindActiveByUserIdIsCalled_thenReturnsOnlyActiveOnes() {
        LocalDateTime now = LocalDateTime.now();
        RefreshToken active = RefreshToken.issue(UUID.randomUUID(), userId, now, now.plusDays(7));
        RefreshToken revoked = RefreshToken.issue(UUID.randomUUID(), userId, now, now.plusDays(7));
        revoked.revoke(now, null);
        adapter.save(active);
        adapter.save(revoked);

        List<RefreshToken> found = adapter.findActiveByUserId(userId);

        assertThat(found).extracting(RefreshToken::getJti).containsExactly(active.getJti());
    }
}
