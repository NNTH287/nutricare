package com.nutricare.nutricare_api.core.domain.entity.calculation;

import java.util.Objects;

public class NutrientTarget {
    private Integer nutrientId;
    private Double targetAmountG;

    public NutrientTarget(Integer nutrientId, Double targetAmountG) {
        if(nutrientId == null) {
            throw new InvalidNutrientTargetException("nutrientId is required");
        }
        if(targetAmountG == null || targetAmountG <= 0) {
            throw new InvalidNutrientTargetException("targetAmountG must be positive");
        }
        this.nutrientId = nutrientId;
        this.targetAmountG = targetAmountG;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public Double getTargetAmountG() {
        return targetAmountG;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NutrientTarget that)) return false;
        return Objects.equals(nutrientId, that.nutrientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nutrientId);
    }
}
