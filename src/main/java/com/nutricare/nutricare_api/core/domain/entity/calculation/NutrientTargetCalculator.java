package com.nutricare.nutricare_api.core.domain.entity.calculation;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NutrientTargetCalculator {
    public static NutrientCalculation calculate(Profile profile, List<NutrientRequirement> requirements) {
        Set<NutrientTarget> resolved = new HashSet<>();
        Set<Integer> unresolved = new HashSet<>();
        for (var r : requirements) {
            if(r.matches(profile)) {
                Double amount = r.getRecommendedVal();
                if (amount == null) {
                    unresolved.add(r.getNutrientId());
                }
                else resolved.add(new NutrientTarget(r.getNutrientId(), amount));
            }
        }
        return new NutrientCalculation(resolved, unresolved);
    }

    public static double resolveCalorieTarget(Profile profile, Integer standardId, List<EnergyCoefficient> coefficients) {
        return coefficients.stream()
                .filter(c -> c.matches(profile))
                .findFirst()
                .map(c -> c.resolveTarget(profile))
                .orElseThrow(() -> new UnresolvableCalorieTargetException(profile.getId(), standardId));
    }
}
