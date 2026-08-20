package com.nutricare.nutricare_api.core.domain.entity.calculation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class CalculationResult {
    private Integer id;
    private Integer profileId;
    private Integer standardId;
    private LocalDateTime calculatedAt;
    private Double calorieTarget;
    private Set<NutrientTarget> nutrientTargets;

    public static CalculationResult create(Integer profileId, Integer standardId, Double calorieTarget, Set<NutrientTarget> nutrientTargets) {
        return new CalculationResult(null, profileId, standardId, LocalDateTime.now(), calorieTarget, nutrientTargets);
    }

    public static CalculationResult constitute(Integer id, Integer profileId, Integer standardId, LocalDateTime calculatedAt, Double calorieTarget, Set<NutrientTarget> nutrientTargets) {
        return new CalculationResult(id, profileId, standardId, calculatedAt, calorieTarget, nutrientTargets);
    }

    private CalculationResult(Integer id, Integer profileId, Integer standardId, LocalDateTime calculatedAt, Double calorieTarget, Set<NutrientTarget> nutrientTargets) {
        if(profileId == null) {
            throw new InvalidCalculationResultException("profileId is required");
        }
        if(standardId == null) {
            throw new InvalidCalculationResultException("standardId is required");
        }
        if(calorieTarget == null || calorieTarget <= 0) {
            throw new InvalidCalculationResultException("calorieTarget must be positive");
        }
        this.id = id;
        this.profileId = profileId;
        this.standardId = standardId;
        this.calculatedAt = calculatedAt;
        this.calorieTarget = calorieTarget;
        this.nutrientTargets = new HashSet<>(nutrientTargets);
    }

    public Integer getId() {
        return id;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public Integer getStandardId() {
        return standardId;
    }

    public LocalDateTime getCalculatedAt() {
        return calculatedAt;
    }

    public Double getCalorieTarget() {
        return calorieTarget;
    }

    public Set<NutrientTarget> getNutrientTargets() {
        return Collections.unmodifiableSet(nutrientTargets);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CalculationResult that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
