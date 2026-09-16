package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.*;

import java.util.List;
import java.util.Optional;

public interface ManageFoodItemUseCase {
    List<FoodItemResult> list(int pageIndex, int pageSize);
    Optional<FoodItemDetailsResult> findById(Integer id);
    FoodItemDetailsResult create(CreateFoodItemCommand command);
    FoodItemDetailsResult update(UpdateFoodItemCommand command);
    void deleteById(Integer id);
}
