package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public record GroupTypeMatches(GroupType groupType) implements ProfileSpecification {
    @Override
    public boolean isSatisfiedBy(Profile profile) {
        return groupType == profile.getGroupType();
    }
}
