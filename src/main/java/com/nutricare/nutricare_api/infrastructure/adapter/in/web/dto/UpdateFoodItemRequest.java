package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.util.Set;

public record UpdateFoodItemRequest(String name, String category, Double servingSizeG, Set<String> tags, Set<NutrientAmountInput> nutrients) {
}
