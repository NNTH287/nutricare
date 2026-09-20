package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutrientRequirementJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NutrientRequirementJpaRepository extends JpaRepository<NutrientRequirementJpaEntity, Integer> {
    List<NutrientRequirementJpaEntity> findByStandardId(Integer standardId);
}
