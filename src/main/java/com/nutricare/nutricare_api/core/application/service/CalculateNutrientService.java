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
    private final NutrientTargetCalculator nutrientTargetCalculator;
    private final CalculationResultMapper mapper;

    public CalculateNutrientService(NutrientRequirementRepository requirementRepository,
                                    EnergyCoefficientRepository energyCoefficientRepository,
                                    NutritionStandardRepository standardRepository,
                                    ProfileRepository profileRepository,
                                    CalculationResultRepository calculationResultRepository,
                                    NutrientTargetCalculator nutrientTargetCalculator,
                                    CalculationResultMapper mapper) {
        this.requirementRepository = requirementRepository;
        this.energyCoefficientRepository = energyCoefficientRepository;
        this.standardRepository = standardRepository;
        this.profileRepository = profileRepository;
        this.calculationResultRepository = calculationResultRepository;
        this.nutrientTargetCalculator = nutrientTargetCalculator;
        this.mapper = mapper;
    }

    @Override
    public CalculateNutrientResult calculateNutrientResult(Integer profileId, Integer nutrientStandardId, Integer authenticatedUserId) {
        return mapper.toDto(buildCalculationResult(profileId, nutrientStandardId, authenticatedUserId));
    }

    @Override
    public CalculateNutrientResult saveCalculationResult(Integer profileId, Integer nutrientStandardId, Integer authenticatedUserId) {
        CalculationResult result = calculationResultRepository.save(buildCalculationResult(profileId, nutrientStandardId, authenticatedUserId));
        return mapper.toDto(result);
    }

    private CalculationResult buildCalculationResult(Integer profileId, Integer nutrientStandardId, Integer authenticatedUserId) {
        Profile profile = profileRepository.getById(profileId);
        profile.verifyOwnedBy(authenticatedUserId);
        NutritionStandard standard = standardRepository.getById(nutrientStandardId);
        List<NutrientRequirement> requirements = requirementRepository.findByStandard(standard.getId());
        List<EnergyCoefficient> energyCoefficients = energyCoefficientRepository.findByStandard(standard.getId());

        NutrientCalculation nutrientCalculation = nutrientTargetCalculator.calculate(profile, requirements);
        double calorieTarget = nutrientTargetCalculator.resolveCalorieTarget(profile, standard.getId(), energyCoefficients);

        return CalculationResult.create(profile.getId(), standard.getId(), calorieTarget,
                nutrientCalculation.getResolvedTargets(), nutrientCalculation.getUnresolvedTargets());
    }
}
