package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.core.application.mapper.CalculationResultMapper;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;
import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientRequirement;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTarget;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTargetCalculator;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import com.nutricare.nutricare_api.core.domain.entity.profile.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateNutrientServiceTest {

    @Mock
    private NutrientRequirementRepository requirementRepository;

    @Mock
    private EnergyCoefficientRepository energyCoefficientRepository;

    @Mock
    private NutritionStandardRepository standardRepository;

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private CalculationResultRepository calculationResultRepository;

    @Mock
    private NutrientRepository nutrientRepository;

    private CalculateNutrientService service;

    private static Profile ownedProfile(Integer userId) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(1, userId, "Jane", GroupType.ADULT, SexType.FEMALE,
                LocalDate.now().minusYears(30), 60.0, 165.0, ActivityLevel.MODERATE, null, Set.of(), now, now);
    }

    @BeforeEach
    void setUp() {
        service = new CalculateNutrientService(requirementRepository, energyCoefficientRepository,
                standardRepository, profileRepository, calculationResultRepository, new NutrientTargetCalculator(),
                new CalculationResultMapper(standardRepository, nutrientRepository));
    }

    @Test
    void givenAuthenticatedUserIsNotProfileOwner_whenCalculateNutrientResultIsCalled_thenThrowsProfileAccessDeniedException() {
        Profile profile = ownedProfile(10);
        when(profileRepository.getById(1)).thenReturn(profile);

        assertThatThrownBy(() -> service.calculateNutrientResult(1, 1, 999))
                .isInstanceOf(ProfileAccessDeniedException.class);

        verifyNoInteractions(standardRepository, requirementRepository, energyCoefficientRepository);
    }

    @Test
    void givenOwnedProfileAndResolvableTargets_whenCalculateNutrientResultIsCalled_thenReturnsMappedResultWithoutPersisting() {
        Profile profile = ownedProfile(10);
        NutritionStandard standard = new NutritionStandard(1, "VN-RDA", "Vietnam RDA", "desc");
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, 10.0, 20.0);
        when(profileRepository.getById(1)).thenReturn(profile);
        when(standardRepository.getById(1)).thenReturn(standard);
        when(requirementRepository.findByStandard(1)).thenReturn(List.of(requirement));
        when(energyCoefficientRepository.findByStandard(1)).thenReturn(List.of(coefficient));
        when(nutrientRepository.findById(5)).thenReturn(Optional.of(Nutrient.reconstitute(5, "protein", "Protein", "g")));

        CalculateNutrientResult result = service.calculateNutrientResult(1, 1, 10);

        assertThat(result.standard()).isEqualTo("Vietnam RDA");
        verifyNoInteractions(calculationResultRepository);
    }

    @Test
    void givenOwnedProfileAndResolvableTargets_whenSaveCalculationResultIsCalled_thenPersistsCalculationCarryingComputedTargets() {
        Profile profile = ownedProfile(10);
        NutritionStandard standard = new NutritionStandard(1, "VN-RDA", "Vietnam RDA", "desc");
        EnergyCoefficient coefficient = EnergyCoefficient.create(1, GroupType.ADULT, SexType.FEMALE, 0, 1200, 10.0, 6.25, 5.0);
        NutrientRequirement requirement = NutrientRequirement.create(1, GroupType.ADULT, 5, 0, 1200, 10.0, 20.0);
        when(profileRepository.getById(1)).thenReturn(profile);
        when(standardRepository.getById(1)).thenReturn(standard);
        when(requirementRepository.findByStandard(1)).thenReturn(List.of(requirement));
        when(energyCoefficientRepository.findByStandard(1)).thenReturn(List.of(coefficient));
        when(nutrientRepository.findById(5)).thenReturn(Optional.of(Nutrient.reconstitute(5, "protein", "Protein", "g")));
        when(calculationResultRepository.save(any(CalculationResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CalculateNutrientResult result = service.saveCalculationResult(1, 1, 10);

        assertThat(result.standard()).isEqualTo("Vietnam RDA");

        ArgumentCaptor<CalculationResult> captor = ArgumentCaptor.forClass(CalculationResult.class);
        verify(calculationResultRepository).save(captor.capture());
        CalculationResult persisted = captor.getValue();
        assertThat(persisted.getProfileId()).isEqualTo(1);
        assertThat(persisted.getStandardId()).isEqualTo(1);
        assertThat(persisted.getCalorieTarget()).isGreaterThan(0);
        assertThat(persisted.getNutrientTargets())
                .extracting(NutrientTarget::getNutrientId)
                .containsExactly(5);
    }
}
