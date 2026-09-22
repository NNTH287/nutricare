package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.profile.ActivityLevel;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.ProfilePersistenceMapper;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.ProfileJpaEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class ProfileRepositoryAdapterTest {

    @Autowired
    private ProfileJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private ProfileRepositoryAdapter adapter;
    private int userId;

    @BeforeEach
    void setUp() {
        adapter = new ProfileRepositoryAdapter(jpaRepository, new ProfilePersistenceMapper() {});
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            var statement = connection.prepareStatement(
                    "insert into users (email, password_hash, role, created_at, updated_at) "
                            + "values ('owner@example.com', 'hash', 'USER', now(), now())",
                    Statement.RETURN_GENERATED_KEYS);
            return statement;
        }, keyHolder);
        userId = keyHolder.getKey().intValue();
    }

    @Test
    void givenPersistedProfile_whenGetByIdIsCalled_thenReturnsMatchingDomainProfile() {
        ProfileJpaEntity entity = new ProfileJpaEntity();
        entity.setUserId(userId);
        entity.setName("Jane");
        entity.setGroupType(GroupType.ADULT);
        entity.setSexType(SexType.FEMALE);
        entity.setBirthDate(LocalDate.of(1990, 1, 1));
        entity.setWeightKg(60.0);
        entity.setHeightCm(165.0);
        entity.setActivityLevel(ActivityLevel.MODERATE);
        entity.setTrimester(null);
        entity.setConditions(Set.of("diabetes"));
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        ProfileJpaEntity saved = jpaRepository.saveAndFlush(entity);

        Profile profile = adapter.getById(saved.getId());

        assertThat(profile.getId()).isEqualTo(saved.getId());
        assertThat(profile.getUserId()).isEqualTo(userId);
        assertThat(profile.getName()).isEqualTo("Jane");
        assertThat(profile.getGroupType()).isEqualTo(GroupType.ADULT);
        assertThat(profile.getSexType()).isEqualTo(SexType.FEMALE);
        assertThat(profile.getConditions()).containsExactly("diabetes");
    }

    @Test
    void givenNoProfileWithGivenId_whenGetByIdIsCalled_thenThrowsNoSuchElementException() {
        assertThatThrownBy(() -> adapter.getById(-1))
                .isInstanceOf(NoSuchElementException.class);
    }
}
