package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutrientJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NutrientPersistenceMapper {
    default Nutrient toDomain(NutrientJpaEntity entity) {
        return Nutrient.reconstitute(entity.getId(), entity.getCode(), entity.getName(), entity.getUnit());
    }

    NutrientJpaEntity toEntity(Nutrient domain);
}
