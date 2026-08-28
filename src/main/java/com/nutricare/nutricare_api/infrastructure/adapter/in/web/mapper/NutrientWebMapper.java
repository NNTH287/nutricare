package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.NutrientResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateNutrientCommand;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateNutrientRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.NutrientResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateNutrientRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NutrientWebMapper {
    CreateNutrientCommand toCreateCommand(CreateNutrientRequest request);

    @Mapping(target = "id", source = "id")
    UpdateNutrientCommand toUpdateCommand(Integer id, UpdateNutrientRequest request);

    NutrientResponse toResponse(NutrientResult result);
}
