package com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper;

import com.nutricare.nutricare_api.core.application.dto.*;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodItemWebMapper {
    CreateFoodItemCommand toCreateCommand(CreateFoodItemRequest request);

    @Mapping(target = "id", source = "id")
    UpdateFoodItemCommand toUpdateCommand(Integer id, UpdateFoodItemRequest request);

    FoodItemResponse toResponse(FoodItemResult result);

    List<FoodItemResponse> toResponseList(List<FoodItemResult> results);

    FoodItemDetailsResponse toDetailsResponse(FoodItemDetailsResult result);
}
