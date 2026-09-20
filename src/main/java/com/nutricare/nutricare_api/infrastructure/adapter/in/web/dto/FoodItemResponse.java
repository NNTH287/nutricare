package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.util.Set;

public record FoodItemResponse(Integer id, String name, String category, Set<String> tags) {
}
