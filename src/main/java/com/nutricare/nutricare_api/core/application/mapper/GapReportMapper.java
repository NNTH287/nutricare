package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;
import com.nutricare.nutricare_api.core.application.dto.NutrientGapResult;
import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientGap;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class GapReportMapper {
    private final NutrientRepository nutrientRepository;

    public GapReportMapper(NutrientRepository nutrientRepository) {
        this.nutrientRepository = nutrientRepository;
    }

    public GapReportResult toDto(String standardName, LocalDate date, Double consumedCalories, Double calorieTarget,
                                  Set<NutrientGap> gaps, Set<Integer> unresolvedNutrientIds) {
        return new GapReportResult(standardName, date, LocalDateTime.now(), consumedCalories, calorieTarget,
                convertFromNutrientGaps(gaps), convertUnresolvedNutrientIds(unresolvedNutrientIds));
    }

    private Set<String> convertUnresolvedNutrientIds(Set<Integer> nutrientIds) {
        return nutrientIds.stream()
                .map(id -> nutrientRepository.findById(id).orElseThrow())
                .map(Nutrient::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private NutrientGapResult convertFromNutrientGap(NutrientGap gap) {
        Nutrient nutrient = nutrientRepository.findById(gap.getNutrientId()).orElseThrow();

        return new NutrientGapResult(nutrient.getName(), nutrient.getUnit(), gap.getConsumedAmountG(),
                gap.getTarget().getTargetAmountG(), gap.getTarget().getMaxAmountG(),
                gap.getStatus().name(), gap.getShortfallOrOverAmountG());
    }

    private Set<NutrientGapResult> convertFromNutrientGaps(Set<NutrientGap> gaps) {
        return gaps.stream().map(this::convertFromNutrientGap).collect(Collectors.toUnmodifiableSet());
    }
}
