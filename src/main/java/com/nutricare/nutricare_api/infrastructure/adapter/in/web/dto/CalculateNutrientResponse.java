package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.time.LocalDateTime;
import java.util.Set;

public record CalculateNutrientResponse(String standard, LocalDateTime calculatedAt, Double calorieTarget,
                                        Set<NutrientTargetResponse> nutrientTargets, Set<String> unresolvedNutrients) {
}
