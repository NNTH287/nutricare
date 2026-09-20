package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.mapper.FoodItemMapper;
import com.nutricare.nutricare_api.core.application.port.in.ManageFoodItemUseCase;
import com.nutricare.nutricare_api.core.application.port.out.FoodItemRepository;
import com.nutricare.nutricare_api.core.application.service.ManageFoodItemService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FoodItemConfig {

    @Bean
    public FoodItemMapper foodItemMapper() {
        return new FoodItemMapper();
    }

    @Bean
    public ManageFoodItemUseCase manageFoodItemUseCase(FoodItemRepository foodItemRepository, FoodItemMapper foodItemMapper) {
        return new ManageFoodItemService(foodItemRepository, foodItemMapper);
    }
}