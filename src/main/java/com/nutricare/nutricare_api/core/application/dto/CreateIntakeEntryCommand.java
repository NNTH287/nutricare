package com.nutricare.nutricare_api.core.application.dto;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

public record CreateIntakeEntryCommand(Integer intakeLogId, Integer foodItemId, Double quantityG, MealSlot mealSlot) {
}
