package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

public record NutrientGapResponse(String name, String unit, Double consumedAmount, Double targetAmount,
                                  Double maxAmount, String status, Double amount) {
}
