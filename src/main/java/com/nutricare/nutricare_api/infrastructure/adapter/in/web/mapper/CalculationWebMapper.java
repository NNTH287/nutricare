package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CalculateNutrientResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CalculationWebMapper {
    CalculateNutrientResponse toResponse(CalculateNutrientResult result);
}
