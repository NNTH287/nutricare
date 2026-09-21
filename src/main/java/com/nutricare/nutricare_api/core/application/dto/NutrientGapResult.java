package com.nutricare.nutricare_api.core.application.dto;

public record NutrientGapResult(String name, String unit, Double consumedAmount, Double targetAmount,
                                Double maxAmount, String status, Double amount) {
}
