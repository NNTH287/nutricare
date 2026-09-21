package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.core.application.mapper.CalculationResultMapper;
import com.nutricare.nutricare_api.core.application.port.in.CalculateNutrientUseCase;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.domain.entity.calculation.*;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

import java.util.List;

public class CalculateNutrientService implements CalculateNutrientUseCase {
    private final NutrientRequirementRepository requirementRepository;
    private final EnergyCoefficientRepository energyCoefficientRepository;
    private final NutritionStandardRepository standardRepository;
    private final ProfileRepository profileRepository;
    private final CalculationResultRepository calculationResultRepository;
    private final CalculationResultMapper mapper;

    public CalculateNutrientService(NutrientRequirementRepository requirementRepository,
                                    EnergyCoefficientRepository energyCoefficientRepository,
                                    NutritionStandardRepository standardRepository,
                                    ProfileRepository profileRepository,
                                    CalculationResultRepository calculationResultRepository,
                                    CalculationResultMapper mapper) {
        this.requirementRepository = requirementRepository;
        this.energyCoefficientRepository = energyCoefficientRepository;
        this.standardRepository = standardRepository;
        this.profileRepository = profileRepository;
        this.calculationResultRepository = calculationResultRepository;
        this.mapper = mapper;
    }

    @Override
    public CalculateNutrientResult calculateNutrientResult(Integer profileId, Integer nutrientStandardId) {
        return mapper.toDto(buildCalculationResult(profileId, nutrientStandardId));
    }

    @Override
    public CalculateNutrientResult saveCalculationResult(Integer profileId, Integer nutrientStandardId) {
        CalculationResult result = calculationResultRepository.save(buildCalculationResult(profileId, nutrientStandardId));
        return mapper.toDto(result);
    }

    private CalculationResult buildCalculationResult(Integer profileId, Integer nutrientStandardId) {
        Profile profile = profileRepository.getById(profileId);
        NutritionStandard standard = standardRepository.getById(nutrientStandardId);
        List<NutrientRequirement> requirements = requirementRepository.findByStandard(standard.getId());
        List<EnergyCoefficient> energyCoefficients = energyCoefficientRepository.findByStandard(standard.getId());

        NutrientCalculation nutrientCalculation = NutrientTargetCalculator.calculate(profile, requirements);
        double calorieTarget = NutrientTargetCalculator.resolveCalorieTarget(profile, standard.getId(), energyCoefficients);

        return CalculationResult.create(profile.getId(), standard.getId(), calorieTarget,
                nutrientCalculation.getResolvedTargets(), nutrientCalculation.getUnresolvedTargets());
    }
}
