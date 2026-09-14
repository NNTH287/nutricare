package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;

import java.util.List;
import java.util.Optional;

public interface FoodItemRepository {
    List<FoodItem> findAll(int pageIndex, int pageSize);

    Optional<FoodItem> findById(Integer id);

    FoodItem save(FoodItem foodItem);

    void deleteById(Integer id);
}
