package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodNutrient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.FoodItemJpaEntity;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.FoodNutrientEmbeddable;
import org.mapstruct.Mapper;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface FoodItemPersistenceMapper {
    default FoodItem toDomain(FoodItemJpaEntity entity) {
        Set<FoodNutrient> nutrients = entity.getNutrients().stream()
                .map(n -> new FoodNutrient(entity.getId(), n.getNutrientId(), n.getAmountPer100g()))
                .collect(Collectors.toSet());
        return FoodItem.reconstitute(entity.getId(), entity.getName(), entity.getCategory(), entity.getServingSizeG(),
                entity.getTags(), entity.getOwnerProfileId(), nutrients);
    }

    default FoodItemJpaEntity toEntity(FoodItem domain) {
        Set<FoodNutrientEmbeddable> nutrients = domain.getNutrients().stream()
                .map(n -> new FoodNutrientEmbeddable(n.getNutrientId(), n.getAmountPer100g()))
                .collect(Collectors.toSet());
        return new FoodItemJpaEntity(domain.getId(), domain.getName(), domain.getCategory(), domain.getServingSizeG(),
                domain.getOwnerProfileId(), domain.getTags(), nutrients);
    }
}
