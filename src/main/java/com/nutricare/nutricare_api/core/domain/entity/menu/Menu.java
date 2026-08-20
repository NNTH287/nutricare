package com.nutricare.nutricare_api.core.domain.entity.menu;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Menu {
    private Integer id;
    private Integer profileId;
    private LocalDate menuDate;
    private String label;
    private MenuStatus menuStatus = MenuStatus.DRAFT;
    private Set<MenuItem> items = new HashSet<>();

    public Menu(Integer profileId, LocalDate menuDate, String label) {
        this.profileId = profileId;
        this.menuDate = menuDate;
        this.label = label;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public LocalDate getMenuDate() {
        return menuDate;
    }

    public void setMenuDate(LocalDate menuDate) {
        this.menuDate = menuDate;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public MenuStatus getMenuStatus() {
        return menuStatus;
    }

    public void setMenuStatus(MenuStatus menuStatus) {
        this.menuStatus = menuStatus;
    }

    public Set<MenuItem> getItems() {
        return Collections.unmodifiableSet(items);
    }

    public MenuItem addItem(Integer foodItemId, MealSlot mealSlot, Double quantityG) {
        if (menuStatus == MenuStatus.FINAL) {
            throw new InvalidMenuException("cannot modify a finalized menu");
        }
        MenuItem item = new MenuItem(foodItemId, mealSlot, quantityG);
        if (!items.add(item)) {
            throw new InvalidMenuItemException("item already exists for this meal slot");
        }
        return item;
    }

    public MenuItem updateItem(Integer foodItemId, MealSlot mealSlot, Double newQuantityG) {
        if (menuStatus == MenuStatus.FINAL) {
            throw new InvalidMenuException("cannot modify a finalized menu");
        }
        MenuItem updated = new MenuItem(foodItemId, mealSlot, newQuantityG);
        if (!items.remove(updated)) {
            throw new InvalidMenuItemException("item does not exist");
        }
        items.add(updated);
        return updated;
    }

    public void removeItem(Integer foodItemId, MealSlot mealSlot) {
        if (menuStatus == MenuStatus.FINAL) {
            throw new InvalidMenuException("cannot modify a finalized menu");
        }
        boolean removed = items.removeIf(i ->
                Objects.equals(i.getFoodItemId(), foodItemId) && i.getMealSlot() == mealSlot);
        if (!removed) {
            throw new InvalidMenuItemException("item does not exist");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Menu menu)) return false;
        return Objects.equals(id, menu.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
