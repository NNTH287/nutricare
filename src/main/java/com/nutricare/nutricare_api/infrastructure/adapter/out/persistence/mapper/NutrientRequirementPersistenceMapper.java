package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientRequirement;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutrientRequirementJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NutrientRequirementPersistenceMapper {
    default NutrientRequirement toDomain(NutrientRequirementJpaEntity entity) {
        return NutrientRequirement.reconstitute(entity.getId(), entity.getStandardId(), entity.getGroupType(),
                entity.getNutrientId(), entity.getAgeMonthsMin(), entity.getAgeMonthsMax(), entity.getTrimester(),
                entity.getRecommendedVal(), entity.getMaxVal());
    }

    NutrientRequirementJpaEntity toEntity(NutrientRequirement domain);
}
