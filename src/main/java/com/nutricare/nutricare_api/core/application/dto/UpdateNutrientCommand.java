package com.nutricare.nutricare_api.core.application.dto;

public record UpdateNutrientCommand(Integer id, String code, String name, String unit) {
}
