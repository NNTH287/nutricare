package com.nutricare.nutricare_api.core.application.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record CalculateNutrientResult(String standard, LocalDateTime calculatedAt, Double calorieTarget,
                                      Set<NutrientTargetResult> nutrientTargets, Set<String> unresolvedNutrients) {
}
