package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.FoodItemDetailsResult;
import com.nutricare.nutricare_api.core.application.dto.FoodItemResult;
import com.nutricare.nutricare_api.core.application.dto.NutrientAmountInput;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodNutrient;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.NutrientAmount;

import java.util.Set;
import java.util.stream.Collectors;

public class FoodItemMapper {
    public Set<NutrientAmount> fromInputsToDomain(Set<NutrientAmountInput> inputs) {
        if (inputs == null) {
            return Set.of();
        }
        return inputs.stream()
                .map(input -> new NutrientAmount(input.nutrientId(), input.amountPer100g()))
                .collect(Collectors.toSet());
    }

    public FoodItemResult toResult(FoodItem foodItem) {
        return new FoodItemResult(foodItem.getId(), foodItem.getName(), foodItem.getCategory(), foodItem.getTags());
    }

    public FoodItemDetailsResult toDetails(FoodItem foodItem) {
        Set<NutrientAmountInput> nutrients = foodItem.getNutrients().stream()
                .map(this::toNutrientAmountInput)
                .collect(Collectors.toSet());
        return new FoodItemDetailsResult(foodItem.getId(), foodItem.getName(), foodItem.getCategory(),
                foodItem.getServingSizeG(), foodItem.getTags(), foodItem.getOwnerProfileId(), nutrients);
    }

    private NutrientAmountInput toNutrientAmountInput(FoodNutrient foodNutrient) {
        return new NutrientAmountInput(foodNutrient.getNutrientId(), foodNutrient.getAmountPer100g());
    }
}
