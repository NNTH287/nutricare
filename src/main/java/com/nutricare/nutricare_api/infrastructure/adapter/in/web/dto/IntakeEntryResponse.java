package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

public record IntakeEntryResponse(Integer id, Integer foodItemId, Double quantityG, MealSlot mealSlot) {
}
