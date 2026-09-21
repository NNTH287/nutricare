package com.nutricare.nutricare_api.core.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record GapReportResult(String standard, LocalDate date, LocalDateTime evaluatedAt,
                              Double consumedCalories, Double calorieTarget,
                              Set<NutrientGapResult> nutrientGaps, Set<String> notEvaluableNutrients) {
}
