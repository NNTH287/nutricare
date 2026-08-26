package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.UpdateNutrientCommand;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

import java.util.Optional;

public interface ManageNutrientUseCase {
    Optional<Nutrient> findById(Integer id);
    Integer save(CreateNutrientCommand command);
    Nutrient update(UpdateNutrientCommand command);
    int deleteById(Integer id);
}
