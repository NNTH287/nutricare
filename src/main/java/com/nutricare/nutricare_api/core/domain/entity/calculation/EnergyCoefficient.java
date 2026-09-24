package com.nutricare.nutricare_api.core.domain.entity.calculation;

import com.nutricare.nutricare_api.core.domain.entity.calculation.formula.CalorieFormulaRegistry;
import com.nutricare.nutricare_api.core.domain.entity.calculation.spec.AgeInMonthsWithin;
import com.nutricare.nutricare_api.core.domain.entity.calculation.spec.GroupTypeMatches;
import com.nutricare.nutricare_api.core.domain.entity.calculation.spec.SexMatches;
import com.nutricare.nutricare_api.core.domain.entity.calculation.spec.TrimesterMatches;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.InvalidTrimesterException;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import com.nutricare.nutricare_api.core.domain.entity.profile.TrimesterPolicy;

import java.util.Objects;

public class EnergyCoefficient {
    private Integer id;
    private Integer standardId;
    private GroupType groupType;
    private SexType sexType;
    private Integer ageMonthsMin;
    private Integer ageMonthsMax;
    private Integer trimester;
    private double weightCoefficient;
    private double heightCoefficient;
    private double intercept;

    public static EnergyCoefficient create(Integer standardId, GroupType groupType, SexType sexType,
                                            Integer ageMonthsMin, Integer ageMonthsMax,
                                            double weightCoefficient, double heightCoefficient, double intercept) {
        if (groupType == GroupType.PREGNANT) {
            throw new InvalidTrimesterException("Use createForPregnant() for a PREGNANT profile.");
        }
        return new EnergyCoefficient(null, standardId, groupType, sexType, ageMonthsMin, ageMonthsMax, null,
                weightCoefficient, heightCoefficient, intercept);
    }

    public static EnergyCoefficient createForPregnant(Integer standardId, SexType sexType,
                                                       Integer ageMonthsMin, Integer ageMonthsMax, Integer trimester,
                                                       double weightCoefficient, double heightCoefficient, double intercept) {
        return new EnergyCoefficient(null, standardId, GroupType.PREGNANT, sexType, ageMonthsMin, ageMonthsMax,
                trimester, weightCoefficient, heightCoefficient, intercept);
    }

    public static EnergyCoefficient reconstitute(Integer id, Integer standardId, GroupType groupType, SexType sexType,
                                                  Integer ageMonthsMin, Integer ageMonthsMax, Integer trimester,
                                                  double weightCoefficient, double heightCoefficient, double intercept) {
        return new EnergyCoefficient(id, standardId, groupType, sexType, ageMonthsMin, ageMonthsMax, trimester,
                weightCoefficient, heightCoefficient, intercept);
    }

    private EnergyCoefficient(Integer id, Integer standardId, GroupType groupType, SexType sexType,
                              Integer ageMonthsMin, Integer ageMonthsMax, Integer trimester,
                              double weightCoefficient, double heightCoefficient, double intercept) {
        TrimesterPolicy.validate(groupType, trimester);
        if (standardId == null) {
            throw new InvalidEnergyCoefficientException("standardId is required");
        }
        if (groupType == null) {
            throw new InvalidEnergyCoefficientException("groupType is required");
        }
        if (ageMonthsMin == null || ageMonthsMax == null || ageMonthsMin > ageMonthsMax) {
            throw new InvalidEnergyCoefficientException("ageMonthsMin/ageMonthsMax must be a valid range");
        }
        this.id = id;
        this.standardId = standardId;
        this.groupType = groupType;
        this.sexType = sexType;
        this.ageMonthsMin = ageMonthsMin;
        this.ageMonthsMax = ageMonthsMax;
        this.trimester = trimester;
        this.weightCoefficient = weightCoefficient;
        this.heightCoefficient = heightCoefficient;
        this.intercept = intercept;
    }

    public boolean matches(Profile profile) {
        return new GroupTypeMatches(groupType)
                .and(new SexMatches(sexType))
                .and(new AgeInMonthsWithin(ageMonthsMin, ageMonthsMax))
                .and(new TrimesterMatches(trimester))
                .isSatisfiedBy(profile);
    }

    public double resolveTarget(Profile profile) {
        return CalorieFormulaRegistry.resolve(profile.getGroupType()).dailyCalories(profile, this);
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

    public SexType getSexType() {
        return sexType;
    }

    public Integer getAgeMonthsMin() {
        return ageMonthsMin;
    }

    public Integer getAgeMonthsMax() {
        return ageMonthsMax;
    }

    public Integer getTrimester() {
        return trimester;
    }

    public double getWeightCoefficient() {
        return weightCoefficient;
    }

    public double getHeightCoefficient() {
        return heightCoefficient;
    }

    public double getIntercept() {
        return intercept;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EnergyCoefficient that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? Objects.hashCode(id) : System.identityHashCode(this);
    }
}
