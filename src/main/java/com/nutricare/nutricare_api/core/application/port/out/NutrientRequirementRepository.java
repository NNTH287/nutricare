package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientRequirement;
import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;

import java.util.List;

public interface NutrientRequirementRepository {
    List<NutrientRequirement> findByStandard(Integer standardId);
}
