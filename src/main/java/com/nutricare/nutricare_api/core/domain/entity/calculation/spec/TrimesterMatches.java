package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

import java.util.Objects;

public record TrimesterMatches(Integer trimester) implements ProfileSpecification {
    @Override
    public boolean isSatisfiedBy(Profile profile) {
        return Objects.equals(trimester, profile.getTrimester());
    }
}
