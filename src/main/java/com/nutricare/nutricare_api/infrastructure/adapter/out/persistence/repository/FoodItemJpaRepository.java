package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.FoodItemJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface FoodItemJpaRepository extends JpaRepository<FoodItemJpaEntity, Integer> {
    List<FoodItemJpaEntity> findAllByIdIn(Collection<Integer> ids);
}
