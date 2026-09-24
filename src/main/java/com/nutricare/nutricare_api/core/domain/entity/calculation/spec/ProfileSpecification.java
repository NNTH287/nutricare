package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public interface ProfileSpecification {
    boolean isSatisfiedBy(Profile profile);

    default ProfileSpecification and(ProfileSpecification other) {
        return profile -> this.isSatisfiedBy(profile) && other.isSatisfiedBy(profile);
    }
}
