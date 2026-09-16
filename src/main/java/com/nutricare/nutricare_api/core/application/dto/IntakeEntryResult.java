package com.nutricare.nutricare_api.core.application.dto;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

public record IntakeEntryResult(Integer id, Integer foodItemId, Double quantityG, MealSlot mealSlot) {
}
