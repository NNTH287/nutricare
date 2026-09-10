package com.nutricare.nutricare_api.core.domain.entity.fooditem;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class FoodItem {
    private final Integer id;
    private String name;
    private String category;
    private Double servingSizeG;
    private Set<String> tags;
    private final Integer ownerProfileId;
    private Set<FoodNutrient> nutrients;

    public static FoodItem create(String name, String category, Double servingSizeG, Set<String> tags,
                                   Integer ownerProfileId, Set<NutrientAmount> nutrientAmounts) {
        FoodItem item = new FoodItem(null, name, category, servingSizeG, tags, ownerProfileId);
        item.nutrients = toFoodNutrients(item.id, nutrientAmounts);
        return item;
    }

    public static FoodItem reconstitute(Integer id, String name, String category, Double servingSizeG,
                                         Set<String> tags, Integer ownerProfileId, Set<FoodNutrient> nutrients) {
        FoodItem item = new FoodItem(id, name, category, servingSizeG, tags, ownerProfileId);
        item.nutrients = nutrients == null ? new HashSet<>() : new HashSet<>(nutrients);
        return item;
    }

    private FoodItem(Integer id, String name, String category, Double servingSizeG, Set<String> tags, Integer ownerProfileId) {
        if (name == null || name.isBlank()) {
            throw new InvalidFoodItemException("name is required");
        }
        if (servingSizeG == null || servingSizeG <= 0) {
            throw new InvalidFoodItemException("servingSizeG must be positive");
        }
        this.id = id;
        this.name = name;
        this.category = category;
        this.servingSizeG = servingSizeG;
        this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
        this.ownerProfileId = ownerProfileId;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.isBlank()) {
            throw new InvalidFoodItemException("name is required");
        }
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Double getServingSizeG() {
        return servingSizeG;
    }

    public void setServingSizeG(Double servingSizeG) {
        if (servingSizeG == null || servingSizeG <= 0) {
            throw new InvalidFoodItemException("servingSizeG must be positive");
        }
        this.servingSizeG = servingSizeG;
    }

    public Set<String> getTags() {
        return Collections.unmodifiableSet(tags);
    }

    public void setTags(Set<String> tags) {
        this.tags = tags == null ? new HashSet<>() : new HashSet<>(tags);
    }

    public Integer getOwnerProfileId() {
        return ownerProfileId;
    }

    public Set<FoodNutrient> getNutrients() {
        return Collections.unmodifiableSet(nutrients);
    }

    public void replaceNutrients(Set<NutrientAmount> nutrientAmounts) {
        this.nutrients = toFoodNutrients(this.id, nutrientAmounts);
    }

    private static Set<FoodNutrient> toFoodNutrients(Integer foodItemId, Set<NutrientAmount> nutrientAmounts) {
        if (nutrientAmounts == null || nutrientAmounts.isEmpty()) {
            return new HashSet<>();
        }
        long distinctNutrientIds = nutrientAmounts.stream().map(NutrientAmount::nutrientId).distinct().count();
        if (distinctNutrientIds != nutrientAmounts.size()) {
            throw new InvalidFoodNutrientException("duplicate nutrientId for the same food item");
        }
        return nutrientAmounts.stream()
                .map(na -> new FoodNutrient(foodItemId, na.nutrientId(), na.amountPer100g()))
                .collect(Collectors.toSet());
    }

    public boolean isVisibleTo(Integer profileId) {
        return ownerProfileId == null || ownerProfileId.equals(profileId);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FoodItem foodItem)) return false;
        return Objects.equals(id, foodItem.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
