package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public record GapReportResponse(String standard, LocalDate date, LocalDateTime evaluatedAt,
                                Double consumedCalories, Double calorieTarget,
                                Set<NutrientGapResponse> nutrientGaps, Set<String> notEvaluableNutrients) {
}
