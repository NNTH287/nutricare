package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.NutrientResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateNutrientCommand;

import java.util.Optional;

public interface ManageNutrientUseCase {
    Optional<NutrientResult> findById(Integer id);
    NutrientResult create(CreateNutrientCommand command);
    NutrientResult update(UpdateNutrientCommand command);
    void deleteById(Integer id);
}
