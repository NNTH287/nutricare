package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.NutritionStandardRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.NutritionStandardPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NutritionStandardRepositoryAdapter implements NutritionStandardRepository {
    private final NutritionStandardJpaRepository repository;
    private final NutritionStandardPersistenceMapper mapper;

    @Override
    public NutritionStandard getById(Integer id) {
        return repository.findById(id).map(mapper::toDomain).orElseThrow();
    }
}
