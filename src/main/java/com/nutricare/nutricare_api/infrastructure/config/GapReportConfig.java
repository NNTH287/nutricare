package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.mapper.GapReportMapper;
import com.nutricare.nutricare_api.core.application.port.in.GetIntakeGapReportUseCase;
import com.nutricare.nutricare_api.core.application.port.out.*;
import com.nutricare.nutricare_api.core.application.service.GetIntakeGapReportService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GapReportConfig {

    @Bean
    public GapReportMapper gapReportMapper(NutrientRepository nutrientRepository) {
        return new GapReportMapper(nutrientRepository);
    }

    @Bean
    public GetIntakeGapReportUseCase getIntakeGapReportUseCase(ProfileRepository profileRepository,
                                                                 NutritionStandardRepository standardRepository,
                                                                 IntakeLogRepository intakeLogRepository,
                                                                 FoodItemRepository foodItemRepository,
                                                                 NutrientRequirementRepository requirementRepository,
                                                                 EnergyCoefficientRepository energyCoefficientRepository,
                                                                 NutrientRepository nutrientRepository,
                                                                 GapReportMapper gapReportMapper) {
        return new GetIntakeGapReportService(profileRepository, standardRepository, intakeLogRepository,
                foodItemRepository, requirementRepository, energyCoefficientRepository, nutrientRepository,
                gapReportMapper);
    }
}
