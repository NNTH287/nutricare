package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.NutrientPersistenceMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class NutrientRepositoryAdapterTest {

    @Autowired
    private NutrientJpaRepository jpaRepository;

    private NutrientRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new NutrientRepositoryAdapter(jpaRepository, new NutrientPersistenceMapperImpl());
    }

    @Test
    void givenNewNutrient_whenSaveIsCalled_thenItIsPersistedWithGeneratedId() {
        Nutrient saved = adapter.save(Nutrient.create("protein", "Protein", "g"));

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCode()).isEqualTo("protein");
    }

    @Test
    void givenPersistedNutrient_whenFindByIdIsCalled_thenReturnsMatchingNutrient() {
        Nutrient saved = adapter.save(Nutrient.create("protein", "Protein", "g"));

        Optional<Nutrient> found = adapter.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getName()).isEqualTo("Protein");
    }

    @Test
    void givenNoNutrientWithGivenId_whenFindByIdIsCalled_thenReturnsEmpty() {
        assertThat(adapter.findById(-1)).isEmpty();
    }

    @Test
    void givenPersistedNutrient_whenFindByCodeIsCalled_thenReturnsMatchingNutrient() {
        adapter.save(Nutrient.create("protein", "Protein", "g"));

        Optional<Nutrient> found = adapter.findByCode("protein");

        assertThat(found).isPresent();
        assertThat(found.get().getCode()).isEqualTo("protein");
    }

    @Test
    void givenNoNutrientWithGivenCode_whenFindByCodeIsCalled_thenReturnsEmpty() {
        assertThat(adapter.findByCode("missing")).isEmpty();
    }

    @Test
    void givenPersistedNutrient_whenExistsCodeIsCalled_thenReturnsTrue() {
        adapter.save(Nutrient.create("protein", "Protein", "g"));

        assertThat(adapter.existsCode("protein")).isTrue();
    }

    @Test
    void givenNoNutrientWithGivenCode_whenExistsCodeIsCalled_thenReturnsFalse() {
        assertThat(adapter.existsCode("missing")).isFalse();
    }

    @Test
    void givenPersistedNutrient_whenDeleteByIdIsCalled_thenItIsNoLongerFound() {
        Nutrient saved = adapter.save(Nutrient.create("protein", "Protein", "g"));

        adapter.deleteById(saved.getId());

        assertThat(adapter.findById(saved.getId())).isEmpty();
    }
}
