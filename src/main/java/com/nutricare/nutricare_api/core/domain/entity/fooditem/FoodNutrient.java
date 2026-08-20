package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import java.util.Objects;

public class FoodNutrient {
    private Integer foodItemId;
    private Integer nutrientId;
    private Double amountPer100g;

    public FoodNutrient(Integer foodItemId, Integer nutrientId, Double amountPer100g) {
        if(amountPer100g == null || amountPer100g <= 0) {
            throw new InvalidFoodNutrientException("amountPer100g must be positive");
        }
        this.foodItemId = foodItemId;
        this.nutrientId = nutrientId;
        this.amountPer100g = amountPer100g;
    }

    public Integer getFoodItemId() {
        return foodItemId;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public Double getAmountPer100g() {
        return amountPer100g;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FoodNutrient that)) return false;
        return Objects.equals(foodItemId, that.foodItemId) && Objects.equals(nutrientId, that.nutrientId);
    }

    @Override
    public int hashCode() {
        return (foodItemId != null && nutrientId != null)
                ? Objects.hash(foodItemId, nutrientId)
                : System.identityHashCode(this);
    }
}
