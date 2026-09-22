package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.NutritionStandardPersistenceMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DataJpaTest
class NutritionStandardRepositoryAdapterTest {

    @Autowired
    private NutritionStandardJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private NutritionStandardRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NutritionStandardRepositoryAdapter(jpaRepository, new NutritionStandardPersistenceMapperImpl());
    }

    @Test
    void givenPersistedStandard_whenGetByIdIsCalled_thenReturnsMatchingDomainStandard() {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(
                "insert into nutrition_standard (code, name, description) values ('VN-RDA', 'Vietnam RDA', 'desc')",
                Statement.RETURN_GENERATED_KEYS), keyHolder);
        int standardId = keyHolder.getKey().intValue();

        NutritionStandard standard = adapter.getById(standardId);

        assertThat(standard.getCode()).isEqualTo("VN-RDA");
        assertThat(standard.getName()).isEqualTo("Vietnam RDA");
    }

    @Test
    void givenNoStandardWithGivenId_whenGetByIdIsCalled_thenThrowsNoSuchElementException() {
        assertThatThrownBy(() -> adapter.getById(-1))
                .isInstanceOf(NoSuchElementException.class);
    }
}
