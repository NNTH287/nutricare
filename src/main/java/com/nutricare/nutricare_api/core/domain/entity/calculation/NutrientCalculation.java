package com.nutricare.nutricare_api.core.domain.entity.calculation;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class NutrientCalculation {
    private Set<NutrientTarget> resolvedTargets;
    private Set<Integer> unresolvedTargets;

    public NutrientCalculation(Set<NutrientTarget> resolvedTargets, Set<Integer> unresolvedTargets) {
        this.resolvedTargets = resolvedTargets == null ? new HashSet<>() : new HashSet<>(resolvedTargets);
        this.unresolvedTargets = unresolvedTargets == null ? new HashSet<>() : new HashSet<>(unresolvedTargets);
    }

    public Set<NutrientTarget> getResolvedTargets() {
        return Collections.unmodifiableSet(resolvedTargets);
    }

    public Set<Integer> getUnresolvedTargets() {
        return Collections.unmodifiableSet(unresolvedTargets);
    }
}
