package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.NutrientRequirementRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientRequirement;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.NutrientRequirementPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class NutrientRequirementRepositoryAdapter implements NutrientRequirementRepository {
    private final NutrientRequirementJpaRepository repository;
    private final NutrientRequirementPersistenceMapper mapper;

    @Override
    public List<NutrientRequirement> findByStandard(Integer standardId) {
        return repository.findByStandardId(standardId).stream().map(mapper::toDomain).toList();
    }
}
