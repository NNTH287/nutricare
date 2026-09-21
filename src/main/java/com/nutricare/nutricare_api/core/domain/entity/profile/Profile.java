package com.nutricare.nutricare_api.core.domain.entity.profile;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Profile {
    private Integer id;
    private Integer userId;
    private String name;
    private GroupType groupType;
    private SexType sexType;
    private LocalDate birthDate;
    private double weightKg;
    private double heightCm;
    private ActivityLevel activityLevel;
    private Integer trimester;
    private Set<String> conditions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static Profile create(Integer userId, String name, GroupType groupType, SexType sexType,
                                  LocalDate birthDate, double weightKg, double heightCm,
                                  ActivityLevel activityLevel, Set<String> conditions) {
        if (groupType == GroupType.PREGNANT) {
            throw new InvalidTrimesterException("Use createPregnant() for a PREGNANT profile.");
        }
        LocalDateTime now = LocalDateTime.now();
        return new Profile(null, userId, name, groupType, sexType, birthDate, weightKg, heightCm,
                activityLevel, null, conditions, now, now);
    }

    public static Profile createPregnant(Integer userId, String name, SexType sexType,
                                          LocalDate birthDate, double weightKg, double heightCm,
                                          ActivityLevel activityLevel, Set<String> conditions, int trimester) {
        LocalDateTime now = LocalDateTime.now();
        return new Profile(null, userId, name, GroupType.PREGNANT, sexType, birthDate, weightKg, heightCm,
                activityLevel, trimester, conditions, now, now);
    }

    public static Profile reconstitute(int id, Integer userId, String name, GroupType groupType, SexType sexType,
                                        LocalDate birthDate, double weightKg, double heightCm,
                                        ActivityLevel activityLevel, Integer trimester, Set<String> conditions,
                                        LocalDateTime createdAt, LocalDateTime updatedAt) {
        return new Profile(id, userId, name, groupType, sexType, birthDate, weightKg, heightCm,
                activityLevel, trimester, conditions, createdAt, updatedAt);
    }

    private Profile(Integer id, Integer userId, String name, GroupType groupType, SexType sexType,
                     LocalDate birthDate, double weightKg, double heightCm, ActivityLevel activityLevel,
                     Integer trimester, Set<String> conditions, LocalDateTime createdAt, LocalDateTime updatedAt) {
        TrimesterPolicy.validate(groupType, trimester);
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.groupType = groupType;
        this.sexType = sexType;
        this.birthDate = birthDate;
        this.weightKg = weightKg;
        this.heightCm = heightCm;
        this.activityLevel = activityLevel;
        this.trimester = trimester;
        this.conditions = conditions == null ? new HashSet<>() : new HashSet<>(conditions);
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Integer getId() {
        return id;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        touch();
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public SexType getSexType() {
        return sexType;
    }

    public void setSexType(SexType sexType) {
        this.sexType = sexType;
        touch();
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
        touch();
    }

    public int getAgeInMonths() {
        return (int) ChronoUnit.MONTHS.between(birthDate, LocalDate.now());
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        this.weightKg = weightKg;
        touch();
    }

    public double getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(double heightCm) {
        this.heightCm = heightCm;
        touch();
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
        touch();
    }

    public Integer getTrimester() {
        return trimester;
    }

    public void setTrimester(int trimester) {
        TrimesterPolicy.validate(groupType, trimester);
        this.trimester = trimester;
        touch();
    }

    public Set<String> getConditions() {
        return Collections.unmodifiableSet(conditions);
    }

    public void setConditions(Set<String> conditions) {
        this.conditions = conditions == null ? new HashSet<>() : new HashSet<>(conditions);
        touch();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void verifyOwnedBy(Integer requestingUserId) {
        if (!Objects.equals(userId, requestingUserId)) {
            throw new ProfileAccessDeniedException(
                    "Profile " + id + " is not owned by user " + requestingUserId);
        }
    }

    private void touch() {
        updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Profile profile)) return false;
        if (id == null || profile.id == null) return false;
        return id.equals(profile.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
