package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.calculation.EnergyCoefficient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.EnergyCoefficientJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EnergyCoefficientPersistenceMapper {
    default EnergyCoefficient toDomain(EnergyCoefficientJpaEntity entity) {
        return EnergyCoefficient.reconstitute(entity.getId(), entity.getStandardId(), entity.getGroupType(),
                entity.getSexType(), entity.getAgeMonthsMin(), entity.getAgeMonthsMax(), entity.getTrimester(),
                entity.getWeightCoefficient(), entity.getHeightCoefficient(), entity.getIntercept());
    }

    EnergyCoefficientJpaEntity toEntity(EnergyCoefficient domain);
}
