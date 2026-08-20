package com.nutricare.nutricare_api.core.domain.entity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FoodItem {
    private Integer id;
    private String name;
    private String category;
    private Double servingSizeG;
    private Set<String> tags;
    private Integer ownerProfileId;

    public FoodItem(String name, String category, Double servingSizeG, Set<String> tags, Integer ownerProfileId) {
        if(name == null || name.isBlank()) {
            throw new InvalidFoodItemException("name is required");
        }
        if(servingSizeG == null || servingSizeG <= 0) {
            throw new InvalidFoodItemException("servingSizeG must be positive");
        }
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
        if(name == null || name.isBlank()) {
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
        if(servingSizeG == null || servingSizeG <= 0) {
            throw new InvalidFoodItemException("servingSizeG must be positive");
        }
        return servingSizeG;
    }

    public void setServingSizeG(Double servingSizeG) {
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
