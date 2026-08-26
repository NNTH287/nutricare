package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

public class NutrientMapper {
    public Nutrient fromCreateCommandToDomain(CreateNutrientCommand command) {
        return Nutrient.create(command.code(), command.name(), command.unit());
    }
}
