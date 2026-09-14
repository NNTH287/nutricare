package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.*;

import java.util.List;
import java.util.Optional;

public interface ManageFoodItemUseCase {
    List<FoodItemResult> getFoodItems(int pageIndex, int pageSize);
    Optional<FoodItemDetailsResult> findById(Integer id);
    FoodItemResult create(CreateFoodItemCommand command);
    FoodItemResult update(UpdateFoodItemCommand command);
    void deleteById(Integer id);
}
