package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;

import java.util.List;

public interface EnergyCoefficientRepository {
    List<EnergyCoefficient> findByStandard(Integer standardId);
}
