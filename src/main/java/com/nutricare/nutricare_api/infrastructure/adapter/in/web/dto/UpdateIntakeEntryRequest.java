package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

public record UpdateIntakeEntryRequest(Double quantityG, MealSlot mealSlot) {
}