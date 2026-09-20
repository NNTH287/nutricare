package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.FoodItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodItemJpaRepository extends JpaRepository<FoodItemJpaEntity, Integer> {
}
