package com.nutricare.nutricare_api.core.application.dto;

import java.util.Set;

public record FoodItemResult(Integer id, String name, String category, Set<String> tags) {
}
