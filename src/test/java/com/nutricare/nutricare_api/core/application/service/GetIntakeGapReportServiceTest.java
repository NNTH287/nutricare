package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;
import com.nutricare.nutricare_api.core.application.mapper.GapReportMapper;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.domain.entity.calculation.*;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodNutrient;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import com.nutricare.nutricare_api.core.domain.entity.profile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetIntakeGapReportServiceTest {

    private static final Integer ENERGY_NUTRIENT_ID = 1;
    private static final Integer PROTEIN_NUTRIENT_ID = 2;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private NutritionStandardRepository standardRepository;

    @Mock
    private IntakeLogRepository intakeLogRepository;

    @Mock
    private FoodItemRepository foodItemRepository;

    @Mock
    private NutrientRequirementRepository requirementRepository;

    @Mock
    private EnergyCoefficientRepository energyCoefficientRepository;

    @Mock
    private NutrientRepository nutrientRepository;

    @Mock
    private CalculationResultRepository calculationResultRepository;

    private GetIntakeGapReportService service;

    private static Profile ownedProfile(LocalDateTime updatedAt) {
        return Profile.reconstitute(1, 10, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.of(1990, 1, 1), 60.0, 165.0, ActivityLevel.MODERATE, null, Set.of(),
                updatedAt.minusDays(1), updatedAt);
    }

    private static FoodItem foodItemProvidingEnergy() {
        return FoodItem.reconstitute(5, "Rice", "Grain", 100.0, Set.of(), null,
                Set.of(new FoodNutrient(5, ENERGY_NUTRIENT_ID, 200.0)));
    }

    @BeforeEach
    void setUp() {
        service = new GetIntakeGapReportService(profileRepository, standardRepository, intakeLogRepository,
                foodItemRepository, requirementRepository, energyCoefficientRepository, nutrientRepository,
                calculationResultRepository, new GapReportMapper(nutrientRepository));
    }

    @Test
    void givenAuthenticatedUserIsNotProfileOwner_whenGetGapReportIsCalled_thenThrowsProfileAccessDeniedException() {
        when(profileRepository.getById(1)).thenReturn(ownedProfile(LocalDateTime.now()));

        assertThatThrownBy(() -> service.getGapReport(1, LocalDate.now().minusDays(1), 1, 999))
                .isInstanceOf(ProfileAccessDeniedException.class);

        verifyNoInteractions(standardRepository, intakeLogRepository, calculationResultRepository);
    }

    @Test
    void givenValidStillCachedCalculationResult_whenGetGapReportIsCalled_thenReusesCachedTargetsWithoutRecomputing() {
        LocalDateTime profileUpdatedAt = LocalDateTime.now().minusDays(2);
        Profile profile = ownedProfile(profileUpdatedAt);
        NutritionStandard standard = new NutritionStandard(1, "VN-RDA", "Vietnam RDA", "desc");
        LocalDate date = LocalDate.now().minusDays(1);
        IntakeLog log = IntakeLog.create(1, date);
        log.addEntry(5, 200.0, MealSlot.BREAKFAST);
        CalculationResult cached = CalculationResult.constitute(1, 1, 1, profileUpdatedAt.plusHours(1), 2000.0,
                Set.of(new NutrientTarget(PROTEIN_NUTRIENT_ID, 50.0, 100.0)), Set.of());

        when(profileRepository.getById(1)).thenReturn(profile);
        when(standardRepository.getById(1)).thenReturn(standard);
        when(intakeLogRepository.findByProfileIdAndDate(1, date)).thenReturn(Optional.of(log));
        when(foodItemRepository.findAllByIds(Set.of(5))).thenReturn(List.of(foodItemProvidingEnergy()));
        when(calculationResultRepository.findLatestByProfileIdAndStandardId(1, 1)).thenReturn(Optional.of(cached));
        when(nutrientRepository.findByCode("ENERGY")).thenReturn(Optional.of(Nutrient.reconstitute(ENERGY_NUTRIENT_ID, "ENERGY", "Energy", "kcal")));
        when(nutrientRepository.findById(PROTEIN_NUTRIENT_ID)).thenReturn(Optional.of(Nutrient.reconstitute(PROTEIN_NUTRIENT_ID, "protein", "Protein", "g")));

        GapReportResult result = service.getGapReport(1, date, 1, 10);

        assertThat(result.consumedCalories()).isEqualTo(400.0);
        assertThat(result.calorieTarget()).isEqualTo(2000.0);
        assertThat(result.nutrientGaps()).hasSize(1);
        verifyNoInteractions(requirementRepository, energyCoefficientRepository);
    }

    @Test
    void givenNoCachedCalculationResult_whenGetGapReportIsCalled_thenRecomputesTargetsFromRequirementsAndCoefficients() {
        LocalDateTime profileUpdatedAt = LocalDateTime.now();
        Profile profile = ownedProfile(profileUpdatedAt);
        NutritionStandard standard = new NutritionStandard(1, "VN-RDA", "Vietnam RDA", "desc");
        LocalDate date = LocalDate.now().minusDays(1);
        IntakeLog log = IntakeLog.create(1, date);
        log.addEntry(5, 200.0, MealSlot.BREAKFAST);
        NutrientRequirement proteinRequirement = NutrientRequirement.create(1, GroupType.ADULT, PROTEIN_NUTRIENT_ID, 0, 1200, 50.0, 100.0);
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);

        when(profileRepository.getById(1)).thenReturn(profile);
        when(standardRepository.getById(1)).thenReturn(standard);
        when(intakeLogRepository.findByProfileIdAndDate(1, date)).thenReturn(Optional.of(log));
        when(foodItemRepository.findAllByIds(Set.of(5))).thenReturn(List.of(foodItemProvidingEnergy()));
        when(calculationResultRepository.findLatestByProfileIdAndStandardId(1, 1)).thenReturn(Optional.empty());
        when(requirementRepository.findByStandard(1)).thenReturn(List.of(proteinRequirement));
        when(energyCoefficientRepository.findByStandard(1)).thenReturn(List.of(coefficient));
        when(nutrientRepository.findByCode("ENERGY")).thenReturn(Optional.of(Nutrient.reconstitute(ENERGY_NUTRIENT_ID, "ENERGY", "Energy", "kcal")));
        when(nutrientRepository.findById(PROTEIN_NUTRIENT_ID)).thenReturn(Optional.of(Nutrient.reconstitute(PROTEIN_NUTRIENT_ID, "protein", "Protein", "g")));

        GapReportResult result = service.getGapReport(1, date, 1, 10);

        assertThat(result.consumedCalories()).isEqualTo(400.0);
        assertThat(result.nutrientGaps()).hasSize(1);
        assertThat(result.nutrientGaps().iterator().next().status()).isEqualTo(NutrientGapStatus.SHORTFALL.name());
    }
}
