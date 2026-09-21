package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.CalculationResultRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.CalculationResultPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class CalculationResultRepositoryAdapter implements CalculationResultRepository {
    private final CalculationResultJpaRepository repository;
    private final CalculationResultPersistenceMapper mapper;

    @Override
    public CalculationResult save(CalculationResult result) {
        return mapper.toDomain(repository.save(mapper.toEntity(result)));
    }

    @Override
    public Optional<CalculationResult> findLatestByProfileIdAndStandardId(Integer profileId, Integer standardId) {
        return repository.findFirstByProfileIdAndStandardIdOrderByCalculatedAtDesc(profileId, standardId).map(mapper::toDomain);
    }
}
