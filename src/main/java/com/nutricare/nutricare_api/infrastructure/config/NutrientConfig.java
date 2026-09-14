package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.mapper.NutrientMapper;
import com.nutricare.nutricare_api.core.application.port.in.ManageNutrientUseCase;
import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.application.service.ManageNutrientService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NutrientConfig {

    @Bean
    public NutrientMapper nutrientMapper() {
        return new NutrientMapper();
    }

    @Bean
    public ManageNutrientUseCase manageNutrientUseCase(NutrientRepository nutrientRepository, NutrientMapper nutrientMapper) {
        return new ManageNutrientService(nutrientRepository, nutrientMapper);
    }
}