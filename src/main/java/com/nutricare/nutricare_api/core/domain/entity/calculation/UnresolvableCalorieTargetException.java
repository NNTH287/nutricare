package com.nutricare.nutricare_api.core.domain.entity.calculation;

public class UnresolvableCalorieTargetException extends RuntimeException {
    public UnresolvableCalorieTargetException(Integer profileId, Integer standardId) {
        super("No matching EnergyCoefficient for profileId=%d, standardId=%d".formatted(profileId, standardId));
    }
}
