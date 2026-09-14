package com.nutricare.nutricare_api.core.application.dto;

import java.util.Set;

public record UpdateFoodItemCommand(Integer id, String name, String category, Double servingSizeG, Set<String> tags, Set<NutrientAmountInput> nutrients) {
}
