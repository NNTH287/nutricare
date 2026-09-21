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
    private final GapReportMapper mapper;

    public GetIntakeGapReportService(ProfileRepository profileRepository,
                                      NutritionStandardRepository standardRepository,
                                      IntakeLogRepository intakeLogRepository,
                                      FoodItemRepository foodItemRepository,
                                      NutrientRequirementRepository requirementRepository,
                                      EnergyCoefficientRepository energyCoefficientRepository,
                                      NutrientRepository nutrientRepository,
                                      GapReportMapper mapper) {
        this.profileRepository = profileRepository;
        this.standardRepository = standardRepository;
        this.intakeLogRepository = intakeLogRepository;
        this.foodItemRepository = foodItemRepository;
        this.requirementRepository = requirementRepository;
        this.energyCoefficientRepository = energyCoefficientRepository;
        this.nutrientRepository = nutrientRepository;
        this.mapper = mapper;
    }

    @Override
    public GapReportResult getGapReport(Integer profileId, LocalDate date, Integer nutritionStandardId) {
        Profile profile = profileRepository.getById(profileId);
        NutritionStandard standard = standardRepository.getById(nutritionStandardId);
        IntakeLog log = intakeLogRepository.findByProfileIdAndDate(profileId, date).orElseThrow();

        Map<Integer, Double> consumedAmountByNutrientId = totalConsumedAmounts(log);

        List<NutrientRequirement> requirements = requirementRepository.findByStandard(standard.getId());
        NutrientCalculation calculation = NutrientTargetCalculator.calculate(profile, requirements);

        List<EnergyCoefficient> energyCoefficients = energyCoefficientRepository.findByStandard(standard.getId());
        double calorieTarget = NutrientTargetCalculator.resolveCalorieTarget(profile, standard.getId(), energyCoefficients);

        Integer energyNutrientId = nutrientRepository.findByCode(ENERGY_NUTRIENT_CODE).orElseThrow().getId();
        double consumedCalories = consumedAmountByNutrientId.getOrDefault(energyNutrientId, 0.0);

        Set<NutrientGap> gaps = calculation.getResolvedTargets().stream()
                .map(target -> NutrientGap.evaluate(target.getNutrientId(),
                        consumedAmountByNutrientId.getOrDefault(target.getNutrientId(), 0.0), target))
                .collect(Collectors.toUnmodifiableSet());

        return mapper.toDto(standard.getName(), date, consumedCalories, calorieTarget,
                gaps, calculation.getUnresolvedTargets());
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
