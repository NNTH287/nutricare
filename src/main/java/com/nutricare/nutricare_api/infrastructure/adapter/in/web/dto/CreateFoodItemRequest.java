package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.util.Set;

public record CreateFoodItemRequest(String name, String category, Double servingSizeG, Set<String> tags, Integer ownerProfileId, Set<NutrientAmountInput> nutrients) {
}
