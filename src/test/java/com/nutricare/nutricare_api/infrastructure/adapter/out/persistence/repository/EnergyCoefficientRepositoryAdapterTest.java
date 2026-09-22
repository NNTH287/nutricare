package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.EnergyCoefficientPersistenceMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Statement;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EnergyCoefficientRepositoryAdapterTest {

    @Autowired
    private EnergyCoefficientJpaRepository jpaRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private EnergyCoefficientRepositoryAdapter adapter;
    private int standardId;

    @BeforeEach
    void setUp() {
        adapter = new EnergyCoefficientRepositoryAdapter(jpaRepository, new EnergyCoefficientPersistenceMapperImpl());
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(
                "insert into nutrition_standard (code, name) values ('VN-RDA', 'Vietnam RDA')",
                Statement.RETURN_GENERATED_KEYS), keyHolder);
        standardId = keyHolder.getKey().intValue();
    }

    private void insertEnergyCoefficient(int standardId, GroupType groupType, SexType sexType) {
        jdbcTemplate.update(
                "insert into energy_coefficient (standard_id, group_type, sex_type, age_months_min, age_months_max, "
                        + "weight_coefficient, height_coefficient, intercept) values (?, ?, ?, 0, 1200, 10.0, 6.25, 5.0)",
                standardId, groupType.name(), sexType.name());
    }

    @Test
    void givenCoefficientsForOtherStandards_whenFindByStandardIsCalled_thenReturnsOnlyMatchingCoefficients() {
        insertEnergyCoefficient(standardId, GroupType.ADULT, SexType.FEMALE);
        KeyHolder otherStandardKey = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> connection.prepareStatement(
                "insert into nutrition_standard (code, name) values ('OTHER', 'Other Standard')",
                Statement.RETURN_GENERATED_KEYS), otherStandardKey);
        int otherStandardId = otherStandardKey.getKey().intValue();
        insertEnergyCoefficient(otherStandardId, GroupType.ADULT, SexType.MALE);

        List<EnergyCoefficient> found = adapter.findByStandard(standardId);

        assertThat(found).hasSize(1);
        assertThat(found.get(0).getSexType()).isEqualTo(SexType.FEMALE);
    }

    @Test
    void givenNoCoefficientsForStandard_whenFindByStandardIsCalled_thenReturnsEmptyList() {
        List<EnergyCoefficient> found = adapter.findByStandard(standardId);

        assertThat(found).isEmpty();
    }
}
