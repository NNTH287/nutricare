package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;

import java.util.Optional;

public interface CalculationResultRepository {
    CalculationResult save(CalculationResult result);

    Optional<CalculationResult> findLatestByProfileIdAndStandardId(Integer profileId, Integer standardId);
}
