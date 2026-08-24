package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

public interface ProfileRepository {
    Profile getById(Integer integer);
}
