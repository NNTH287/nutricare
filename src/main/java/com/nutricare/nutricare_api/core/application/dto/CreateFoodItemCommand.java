package com.nutricare.nutricare_api.core.application.dto;

import java.util.Set;

public record CreateFoodItemCommand(String name, String category, Double servingSizeG, Set<String> tags, Integer ownerProfileId, Set<NutrientAmountInput> nutrients) {
}
