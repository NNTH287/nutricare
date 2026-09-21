package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.calculation.CalculationResult;
import com.nutricare.nutricare_api.core.domain.entity.calculation.NutrientTarget;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.CalculationResultJpaEntity;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.NutrientTargetEmbeddable;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CalculationResultPersistenceMapper {
    default CalculationResult toDomain(CalculationResultJpaEntity entity) {
        Set<NutrientTarget> nutrientTargets = entity.getNutrientTargets().stream()
                .map(t -> new NutrientTarget(t.getNutrientId(), t.getTargetAmountG(), t.getMaxAmountG()))
                .collect(Collectors.toSet());
        return CalculationResult.constitute(entity.getId(), entity.getProfileId(), entity.getStandardId(),
                entity.getCalculatedAt(), entity.getCalorieTarget(), nutrientTargets, entity.getUnresolvedNutrientIds());
    }

    default CalculationResultJpaEntity toEntity(CalculationResult domain) {
        Set<NutrientTargetEmbeddable> nutrientTargets = domain.getNutrientTargets().stream()
                .map(t -> new NutrientTargetEmbeddable(t.getNutrientId(), t.getTargetAmountG(), t.getMaxAmountG()))
                .collect(Collectors.toSet());
        return new CalculationResultJpaEntity(domain.getId(), domain.getProfileId(), domain.getStandardId(),
                domain.getCalculatedAt(), domain.getCalorieTarget(), nutrientTargets, domain.getUnresolvedNutrientIds());
    }
}
