package com.nutricare.nutricare_api.core.domain.entity.calculation;

import java.util.Objects;

public class NutrientGap {
    private final Integer nutrientId;
    private final double consumedAmountG;
    private final NutrientTarget target;
    private final NutrientGapStatus status;
    private final Double shortfallOrOverAmountG;

    public static NutrientGap evaluate(Integer nutrientId, double consumedAmountG, NutrientTarget target) {
        NutrientGapStatus status;
        Double amount;
        if (target.getMaxAmountG() != null && consumedAmountG > target.getMaxAmountG()) {
            status = NutrientGapStatus.OVER_INTAKE;
            amount = consumedAmountG - target.getMaxAmountG();
        } else if (consumedAmountG < target.getTargetAmountG()) {
            status = NutrientGapStatus.SHORTFALL;
            amount = target.getTargetAmountG() - consumedAmountG;
        } else {
            status = NutrientGapStatus.ON_TARGET;
            amount = null;
        }
        return new NutrientGap(nutrientId, consumedAmountG, target, status, amount);
    }

    private NutrientGap(Integer nutrientId, double consumedAmountG, NutrientTarget target,
                         NutrientGapStatus status, Double shortfallOrOverAmountG) {
        this.nutrientId = nutrientId;
        this.consumedAmountG = consumedAmountG;
        this.target = target;
        this.status = status;
        this.shortfallOrOverAmountG = shortfallOrOverAmountG;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public double getConsumedAmountG() {
        return consumedAmountG;
    }

    public NutrientTarget getTarget() {
        return target;
    }

    public NutrientGapStatus getStatus() {
        return status;
    }

    public Double getShortfallOrOverAmountG() {
        return shortfallOrOverAmountG;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NutrientGap that)) return false;
        return Objects.equals(nutrientId, that.nutrientId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(nutrientId);
    }
}
