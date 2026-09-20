package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutritionStandardJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionStandardJpaRepository extends JpaRepository<NutritionStandardJpaEntity, Integer> {
}
