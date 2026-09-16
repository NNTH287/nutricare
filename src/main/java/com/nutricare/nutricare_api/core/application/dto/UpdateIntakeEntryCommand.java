package com.nutricare.nutricare_api.core.application.dto;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

public record UpdateIntakeEntryCommand(Integer intakeLogId, Integer entryId, Double quantityG, MealSlot mealSlot) {
}
