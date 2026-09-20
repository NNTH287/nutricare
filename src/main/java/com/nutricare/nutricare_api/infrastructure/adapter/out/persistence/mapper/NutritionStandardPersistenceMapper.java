package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.calculation.NutritionStandard;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutritionStandardJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NutritionStandardPersistenceMapper {
    default NutritionStandard toDomain(NutritionStandardJpaEntity entity) {
        return new NutritionStandard(entity.getId(), entity.getCode(), entity.getName(), entity.getDescription());
    }

    NutritionStandardJpaEntity toEntity(NutritionStandard domain);
}
