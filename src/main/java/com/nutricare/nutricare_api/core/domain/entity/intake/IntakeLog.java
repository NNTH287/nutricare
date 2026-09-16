package com.nutricare.nutricare_api.core.domain.entity.intake;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class IntakeLog {
    private Integer id;
    private Integer profileId;
    private LocalDate logDate;
    private List<IntakeEntry> entries;

    public static IntakeLog create(Integer profileId, LocalDate logDate) {
        return new IntakeLog(null, profileId, logDate, new ArrayList<>());
    }

    public static IntakeLog reconstitute(Integer id, Integer profileId, LocalDate logDate, List<IntakeEntry> entries) {
        return new IntakeLog(id, profileId, logDate, new ArrayList<>(entries));
    }

    private IntakeLog(Integer id, Integer profileId, LocalDate logDate, List<IntakeEntry> entries) {
        if (profileId == null) {
            throw new InvalidIntakeLogException("profileId is required");
        }
        if (logDate == null) {
            throw new InvalidIntakeLogException("logDate is required");
        }
        this.id = id;
        this.profileId = profileId;
        this.logDate = logDate;
        this.entries = entries;
    }

    public Integer getId() {
        return id;
    }

    public Integer getProfileId() {
        return profileId;
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public List<IntakeEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    public IntakeEntry addEntry(Integer foodItemId, Double quantityG, MealSlot mealSlot) {
        IntakeEntry entry = IntakeEntry.create(foodItemId, quantityG, mealSlot);
        entries.add(entry);
        return entry;
    }

    public void updateEntryMealSlot(Integer entryId, MealSlot mealSlot) {
        getEntry(entryId).setMealSlot(mealSlot);
    }

    public void updateEntryQuantity(Integer entryId, Double newQuantityG) {
        getEntry(entryId).setQuantityG(newQuantityG);
    }

    public void removeEntry(Integer entryId) {
        entries.remove(getEntry(entryId));
    }

    public IntakeEntry getEntry(Integer entryId) {
        return entries.stream()
                .filter(e -> Objects.equals(e.getId(), entryId))
                .findFirst()
                .orElseThrow(() -> new InvalidIntakeLogException("entry does not exist"));
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof IntakeLog that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
