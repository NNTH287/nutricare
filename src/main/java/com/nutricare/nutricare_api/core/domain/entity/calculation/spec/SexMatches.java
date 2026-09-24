package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;

import java.util.Objects;

public record SexMatches(SexType sexType) implements ProfileSpecification {
    @Override
    public boolean isSatisfiedBy(Profile profile) {
        return Objects.equals(sexType, profile.getSexType());
    }
}
