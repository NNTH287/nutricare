package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.mapper.CalculationResultMapper;
import com.nutricare.nutricare_api.core.application.port.in.CalculateNutrientUseCase;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.application.service.CalculateNutrientService;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTargetCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CalculationConfig {

    @Bean
    public NutrientTargetCalculator nutrientTargetCalculator() {
        return new NutrientTargetCalculator();
    }

    @Bean
    public CalculationResultMapper calculationResultMapper(NutritionStandardRepository standardRepository,
                                                             NutrientRepository nutrientRepository) {
        return new CalculationResultMapper(standardRepository, nutrientRepository);
    }

    @Bean
    public CalculateNutrientUseCase calculateNutrientUseCase(NutrientRequirementRepository requirementRepository,
                                                               EnergyCoefficientRepository energyCoefficientRepository,
                                                               NutritionStandardRepository standardRepository,
                                                               ProfileRepository profileRepository,
                                                               CalculationResultRepository calculationResultRepository,
                                                               NutrientTargetCalculator nutrientTargetCalculator,
                                                               CalculationResultMapper calculationResultMapper) {
        return new CalculateNutrientService(requirementRepository, energyCoefficientRepository, standardRepository,
                profileRepository, calculationResultRepository, nutrientTargetCalculator, calculationResultMapper);
    }
}
