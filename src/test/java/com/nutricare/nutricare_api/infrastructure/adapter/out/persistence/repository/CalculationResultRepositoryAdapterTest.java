package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTarget;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.CalculationResultPersistenceMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CalculationResultRepositoryAdapterTest {

    @Autowired
    private CalculationResultJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private CalculationResultRepositoryAdapter adapter;
    private int profileId;
    private int standardId;
    private int nutrientId;

    @BeforeEach
    void setUp() {
        adapter = new CalculationResultRepositoryAdapter(jpaRepository, new CalculationResultPersistenceMapper() {});

        int userId = insertAndReturnId(
                "insert into users (email, password_hash, role, created_at, updated_at) "
                        + "values ('owner@example.com', 'hash', 'USER', now(), now())");
        profileId = insertAndReturnId(
                "insert into profile (user_id, name, group_type, sex_type, birth_date, weight_kg, height_cm, "
                        + "activity_level, created_at, updated_at) "
                        + "values (" + userId + ", 'Jane', 'ADULT', 'FEMALE', '1990-01-01', 60.0, 165.0, "
                        + "'MODERATE', now(), now())");
        standardId = insertAndReturnId("insert into nutrition_standard (code, name) values ('VN-RDA', 'Vietnam RDA')");
        nutrientId = insertAndReturnId("insert into nutrient (code, name, unit) values ('protein', 'Protein', 'g')");
    }

    private int insertAndReturnId(String sql) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), keyHolder);
        return keyHolder.getKey().intValue();
    }

    @Test
    void givenNewCalculationResult_whenSaveIsCalled_thenItIsPersistedWithGeneratedId() {
        CalculationResult result = CalculationResult.create(profileId, standardId, 2000.0,
                Set.of(new NutrientTarget(nutrientId, 50.0, 100.0)), Set.of());

        CalculationResult saved = adapter.save(result);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getNutrientTargets()).hasSize(1);
    }

    @Test
    void givenMultipleResultsForProfileAndStandard_whenFindLatestIsCalled_thenReturnsMostRecentlyCalculatedOne() {
        LocalDateTime older = LocalDateTime.now().minusDays(2);
        LocalDateTime newer = LocalDateTime.now().minusDays(1);
        adapter.save(CalculationResult.constitute(null, profileId, standardId, older, 1800.0, Set.of(), Set.of()));
        adapter.save(CalculationResult.constitute(null, profileId, standardId, newer, 2000.0, Set.of(), Set.of()));

        Optional<CalculationResult> found = adapter.findLatestByProfileIdAndStandardId(profileId, standardId);

        assertThat(found).isPresent();
        assertThat(found.get().getCalorieTarget()).isEqualTo(2000.0);
    }

    @Test
    void givenNoResultsForProfileAndStandard_whenFindLatestIsCalled_thenReturnsEmpty() {
        Optional<CalculationResult> found = adapter.findLatestByProfileIdAndStandardId(profileId, standardId);

        assertThat(found).isEmpty();
    }
}
