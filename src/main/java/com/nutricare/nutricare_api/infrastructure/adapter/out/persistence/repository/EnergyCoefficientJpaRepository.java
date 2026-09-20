package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.EnergyCoefficientJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EnergyCoefficientJpaRepository extends JpaRepository<EnergyCoefficientJpaEntity, Integer> {
    List<EnergyCoefficientJpaEntity> findByStandardId(Integer standardId);
}
