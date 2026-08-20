package com.nutricare.nutricare_api.core.domain.entity.intake;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

import java.util.Objects;

public class IntakeEntry {
    private Integer id;
    private Integer foodItemId;
    private Double quantityG;
    private MealSlot mealSlot;

    public static IntakeEntry create(Integer foodItemId, Double quantityG, MealSlot mealSlot) {
        return new IntakeEntry(null, foodItemId, quantityG, mealSlot);
    }

    public static IntakeEntry reconstitute(Integer id, Integer foodItemId, Double quantityG, MealSlot mealSlot) {
        return new IntakeEntry(id, foodItemId, quantityG, mealSlot);
    }

    private IntakeEntry(Integer id, Integer foodItemId, Double quantityG, MealSlot mealSlot) {
        if (foodItemId == null) {
            throw new InvalidIntakeEntryException("foodItemId is required");
        }
        if (quantityG == null || quantityG <= 0) {
            throw new InvalidIntakeEntryException("quantityG must be positive");
        }
        this.id = id;
        this.foodItemId = foodItemId;
        this.quantityG = quantityG;
        this.mealSlot = mealSlot;
    }

    public Integer getId() {
        return id;
    }

    public Integer getFoodItemId() {
        return foodItemId;
    }

    public Double getQuantityG() {
        return quantityG;
    }

    public void setQuantityG(Double quantityG) {
        if (quantityG == null || quantityG <= 0) {
            throw new InvalidIntakeEntryException("quantityG must be positive");
        }
        this.quantityG = quantityG;
    }

    public MealSlot getMealSlot() {
        return mealSlot;
    }

    public void setMealSlot(MealSlot mealSlot) {
        this.mealSlot = mealSlot;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntakeEntry that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
