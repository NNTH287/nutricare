package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

import java.util.Optional;

public interface NutrientRepository {
    Optional<Nutrient> findById(Integer nutrientId);

    boolean existsCode(String code);

    Nutrient save(Nutrient nutrient);

    void deleteById(Integer id);
}
