package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.CalculationResultJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CalculationResultJpaRepository extends JpaRepository<CalculationResultJpaEntity, Integer> {
    Optional<CalculationResultJpaEntity> findFirstByProfileIdAndStandardIdOrderByCalculatedAtDesc(Integer profileId, Integer standardId);
}
