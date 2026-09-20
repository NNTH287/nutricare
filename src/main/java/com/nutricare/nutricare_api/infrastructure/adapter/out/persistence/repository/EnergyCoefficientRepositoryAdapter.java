package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.EnergyCoefficientRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.EnergyCoefficientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class EnergyCoefficientRepositoryAdapter implements EnergyCoefficientRepository {
    private final EnergyCoefficientJpaRepository repository;
    private final EnergyCoefficientPersistenceMapper mapper;

    @Override
    public List<EnergyCoefficient> findByStandard(Integer standardId) {
        return repository.findByStandardId(standardId).stream().map(mapper::toDomain).toList();
    }
}
