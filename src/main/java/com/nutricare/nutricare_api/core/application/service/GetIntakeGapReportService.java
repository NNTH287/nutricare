package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;
import com.nutricare.nutricare_api.core.application.mapper.GapReportMapper;
import com.nutricare.nutricare_api.core.application.port.in.GetIntakeGapReportUseCase;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.domain.entity.calculation.*;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodNutrient;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class GetIntakeGapReportService implements GetIntakeGapReportUseCase {
    private static final String ENERGY_NUTRIENT_CODE = "ENERGY";

    private final ProfileRepository profileRepository;
    private final NutritionStandardRepository standardRepository;
    private final IntakeLogRepository intakeLogRepository;
    private final FoodItemRepository foodItemRepository;
    private final NutrientRequirementRepository requirementRepository;
    private final EnergyCoefficientRepository energyCoefficientRepository;
    private final NutrientRepository nutrientRepository;
    private final CalculationResultRepository calculationResultRepository;
    private final NutrientTargetCalculator nutrientTargetCalculator;
    private final GapReportMapper mapper;

    public GetIntakeGapReportService(ProfileRepository profileRepository,
                                      NutritionStandardRepository standardRepository,
                                      IntakeLogRepository intakeLogRepository,
                                      FoodItemRepository foodItemRepository,
                                      NutrientRequirementRepository requirementRepository,
                                      EnergyCoefficientRepository energyCoefficientRepository,
                                      NutrientRepository nutrientRepository,
                                      CalculationResultRepository calculationResultRepository,
                                      NutrientTargetCalculator nutrientTargetCalculator,
                                      GapReportMapper mapper) {
        this.profileRepository = profileRepository;
        this.standardRepository = standardRepository;
        this.intakeLogRepository = intakeLogRepository;
        this.foodItemRepository = foodItemRepository;
        this.requirementRepository = requirementRepository;
        this.energyCoefficientRepository = energyCoefficientRepository;
        this.nutrientRepository = nutrientRepository;
        this.calculationResultRepository = calculationResultRepository;
        this.nutrientTargetCalculator = nutrientTargetCalculator;
        this.mapper = mapper;
    }

    @Override
    public GapReportResult getGapReport(Integer profileId, LocalDate date, Integer nutritionStandardId, Integer authenticatedUserId) {
        Profile profile = profileRepository.getById(profileId);
        profile.verifyOwnedBy(authenticatedUserId);
        NutritionStandard standard = standardRepository.getById(nutritionStandardId);
        IntakeLog log = intakeLogRepository.findByProfileIdAndDate(profileId, date).orElseThrow();

        Map<Integer, Double> consumedAmountByNutrientId = totalConsumedAmounts(log);

        NutrientTargets targets = resolveNutrientTargets(profile, standard);

        Integer energyNutrientId = nutrientRepository.findByCode(ENERGY_NUTRIENT_CODE).orElseThrow().getId();
        double consumedCalories = consumedAmountByNutrientId.getOrDefault(energyNutrientId, 0.0);

        Set<NutrientGap> gaps = targets.resolvedTargets().stream()
                .map(target -> NutrientGap.evaluate(target.getNutrientId(),
                        consumedAmountByNutrientId.getOrDefault(target.getNutrientId(), 0.0), target))
                .collect(Collectors.toUnmodifiableSet());

        return mapper.toDto(standard.getName(), date, consumedCalories, targets.calorieTarget(),
                gaps, targets.unresolvedNutrientIds());
    }

    private NutrientTargets resolveNutrientTargets(Profile profile, NutritionStandard standard) {
        Optional<CalculationResult> saved = calculationResultRepository
                .findLatestByProfileIdAndStandardId(profile.getId(), standard.getId());
        if (saved.isPresent() && saved.get().isStillValidFor(profile)) {
            CalculationResult result = saved.get();
            return new NutrientTargets(result.getNutrientTargets(), result.getUnresolvedNutrientIds(), result.getCalorieTarget());
        }

        List<NutrientRequirement> requirements = requirementRepository.findByStandard(standard.getId());
        NutrientCalculation calculation = nutrientTargetCalculator.calculate(profile, requirements);

        List<EnergyCoefficient> energyCoefficients = energyCoefficientRepository.findByStandard(standard.getId());
        double calorieTarget = nutrientTargetCalculator.resolveCalorieTarget(profile, standard.getId(), energyCoefficients);

        return new NutrientTargets(calculation.getResolvedTargets(), calculation.getUnresolvedTargets(), calorieTarget);
    }

    private record NutrientTargets(Set<NutrientTarget> resolvedTargets, Set<Integer> unresolvedNutrientIds, double calorieTarget) {
    }

    private Map<Integer, Double> totalConsumedAmounts(IntakeLog log) {
        Set<Integer> foodItemIds = log.getEntries().stream()
                .map(IntakeEntry::getFoodItemId)
                .collect(Collectors.toSet());
        Map<Integer, FoodItem> foodItemsById = foodItemRepository.findAllByIds(foodItemIds).stream()
                .collect(Collectors.toMap(FoodItem::getId, Function.identity()));

        Map<Integer, Double> consumedAmountByNutrientId = new HashMap<>();
        for (IntakeEntry entry : log.getEntries()) {
            FoodItem foodItem = foodItemsById.get(entry.getFoodItemId());
            for (FoodNutrient foodNutrient : foodItem.getNutrients()) {
                double consumed = (entry.getQuantityG() / 100.0) * foodNutrient.getAmountPer100g();
                consumedAmountByNutrientId.merge(foodNutrient.getNutrientId(), consumed, Double::sum);
            }
        }
        return consumedAmountByNutrientId;
    }
}
