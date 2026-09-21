package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutrientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NutrientJpaRepository extends JpaRepository<NutrientJpaEntity, Integer> {
    Optional<NutrientJpaEntity> findByCode(String code);
}
