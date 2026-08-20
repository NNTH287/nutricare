package com.nutricare.nutricare_api.core.domain.entity.menu;

import java.util.Objects;

public class MenuItem {
    private Integer foodItemId;
    private MealSlot mealSlot;
    private Double quantityG;

    MenuItem(Integer foodItemId, MealSlot mealSlot, Double quantityG) {
        if(quantityG == null || quantityG <= 0) {
            throw new InvalidMenuItemException("quantityG must be positive");
        }
        this.foodItemId = foodItemId;
        this.mealSlot = mealSlot;
        this.quantityG = quantityG;
    }

    public Integer getFoodItemId() {
        return foodItemId;
    }

    public MealSlot getMealSlot() {
        return mealSlot;
    }

    public Double getQuantityG() {
        return quantityG;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MenuItem menuItem)) return false;
        return Objects.equals(foodItemId, menuItem.foodItemId)
                && Objects.equals(mealSlot, menuItem.mealSlot);
    }

    @Override
    public int hashCode() {
        return foodItemId != null && mealSlot != null
                ? Objects.hash(foodItemId, mealSlot)
                : System.identityHashCode(this);
    }
}
