package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public record AgeInMonthsWithin(Integer minMonths, Integer maxMonths) implements ProfileSpecification {
    @Override
    public boolean isSatisfiedBy(Profile profile) {
        int ageInMonths = profile.getAgeInMonths();
        return ageInMonths >= minMonths && ageInMonths <= maxMonths;
    }
}
