package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.core.application.dto.NutrientTargetResult;
import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.application.port.out.NutritionStandardRepository;
import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTarget;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

import java.util.Set;
import java.util.stream.Collectors;

public class CalculationResultMapper {
    private final NutritionStandardRepository standardRepository;
    private final NutrientRepository nutrientRepository;

    public CalculationResultMapper(NutritionStandardRepository standardRepository, NutrientRepository nutrientRepository) {
        this.standardRepository = standardRepository;
        this.nutrientRepository = nutrientRepository;
    }

    public CalculateNutrientResult toDto(CalculationResult domain, Set<Integer> unresolvedNutrientIds) {
        NutritionStandard standard = standardRepository.getById(domain.getStandardId());

        return new CalculateNutrientResult(standard.getName(), domain.getCalculatedAt(),
                domain.getCalorieTarget(), convertFromNutrientTargets(domain.getNutrientTargets()),
                convertUnresolvedNutrientIds(unresolvedNutrientIds));
    }

    private Set<String> convertUnresolvedNutrientIds(Set<Integer> nutrientIds) {
        return nutrientIds.stream()
                .map(nutrientRepository::getById)
                .map(Nutrient::getName)
                .collect(Collectors.toUnmodifiableSet());
    }

    private NutrientTargetResult convertFromNutrientTarget(NutrientTarget target) {
        Nutrient nutrient = nutrientRepository.getById(target.getNutrientId());
        Double convertedAmount = target.getTargetAmountG();
        //TODO: convert amount to nutrient's unit

        return new NutrientTargetResult(nutrient.getName(), convertedAmount, nutrient.getUnit());
    }

    private Set<NutrientTargetResult> convertFromNutrientTargets(Set<NutrientTarget> targets) {
        return targets.stream().map(this::convertFromNutrientTarget).collect(Collectors.toUnmodifiableSet());
    }
}
