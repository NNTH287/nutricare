package com.nutricare.nutricare_api.core.domain.entity;

import java.util.Objects;

public class NutrientRequirement {
    private Integer id;
    private Integer standardId;
    private GroupType groupType;
    private Integer nutrientId;
    private Integer ageMonthsMin;
    private Integer ageMonthsMax;
    private Integer trimester;
    private Double recommendedVal;
    private Double maxVal;

    public static NutrientRequirement create(Integer standardId, GroupType groupType, Integer nutrientId,
                                             Integer ageMonthsMin, Integer ageMonthsMax,
                                             Double recommendedVal, Double maxVal) {
        if (groupType == GroupType.PREGNANT) {
            throw new InvalidTrimesterException("Use createForPregnant() for a PREGNANT profile.");
        }

        return new NutrientRequirement(null, standardId, groupType, nutrientId, ageMonthsMin, ageMonthsMax,
                null, recommendedVal, maxVal);
    }

    public static NutrientRequirement createForPregnant(Integer standardId, Integer nutrientId,
                                                        Integer ageMonthsMin, Integer ageMonthsMax, Integer trimester,
                                                        Double recommendedVal, Double maxVal) {
        return new NutrientRequirement(null, standardId, GroupType.PREGNANT, nutrientId, ageMonthsMin, ageMonthsMax,
                trimester, recommendedVal, maxVal);
    }

    public static NutrientRequirement reconstitute(Integer id, Integer standardId, GroupType groupType,
                                                   Integer nutrientId, Integer ageMonthsMin, Integer ageMonthsMax,
                                                   Integer trimester, Double recommendedVal, Double maxVal) {
        return new NutrientRequirement(id, standardId, groupType, nutrientId, ageMonthsMin, ageMonthsMax, trimester,
                recommendedVal, maxVal);
    }

    private NutrientRequirement(Integer id, Integer standardId, GroupType groupType, Integer nutrientId,
                                Integer ageMonthsMin, Integer ageMonthsMax, Integer trimester,
                                Double recommendedVal, Double maxVal) {
        TrimesterPolicy.validate(groupType, trimester);
        this.id = id;
        this.standardId = standardId;
        this.groupType = groupType;
        this.nutrientId = nutrientId;
        this.ageMonthsMin = ageMonthsMin;
        this.ageMonthsMax = ageMonthsMax;
        this.trimester = trimester;
        this.recommendedVal = recommendedVal;
        this.maxVal = maxVal;
    }

    public Integer getId() {
        return id;
    }

    public Integer getStandardId() {
        return standardId;
    }

    public GroupType getGroupType() {
        return groupType;
    }

    public Integer getNutrientId() {
        return nutrientId;
    }

    public Integer getAgeMonthsMin() {
        return ageMonthsMin;
    }

    public void setAgeMonthsMin(Integer ageMonthsMin) {
        this.ageMonthsMin = ageMonthsMin;
    }

    public Integer getAgeMonthsMax() {
        return ageMonthsMax;
    }

    public void setAgeMonthsMax(Integer ageMonthsMax) {
        this.ageMonthsMax = ageMonthsMax;
    }

    public Integer getTrimester() {
        return trimester;
    }

    public void setTrimester(Integer trimester) {
        TrimesterPolicy.validate(groupType, trimester);
        this.trimester = trimester;
    }

    public Double getRecommendedVal() {
        return recommendedVal;
    }

    public void setRecommendedVal(Double recommendedVal) {
        this.recommendedVal = recommendedVal;
    }

    public Double getMaxVal() {
        return maxVal;
    }

    public void setMaxVal(Double maxVal) {
        this.maxVal = maxVal;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof NutrientRequirement that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
