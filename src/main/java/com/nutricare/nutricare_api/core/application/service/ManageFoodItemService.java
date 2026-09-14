package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateFoodItemCommand;
import com.nutricare.nutricare_api.core.application.dto.FoodItemDetailsResult;
import com.nutricare.nutricare_api.core.application.dto.FoodItemResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateFoodItemCommand;
import com.nutricare.nutricare_api.core.application.mapper.FoodItemMapper;
import com.nutricare.nutricare_api.core.application.port.in.ManageFoodItemUseCase;
import com.nutricare.nutricare_api.core.application.port.out.FoodItemRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;

import java.util.List;
import java.util.Optional;

public class ManageFoodItemService implements ManageFoodItemUseCase {

    private final FoodItemRepository foodItemRepository;
    private final FoodItemMapper mapper;

    public ManageFoodItemService(FoodItemRepository foodItemRepository, FoodItemMapper mapper) {
        this.foodItemRepository = foodItemRepository;
        this.mapper = mapper;
    }

    @Override
    public List<FoodItemResult> getFoodItems(int pageIndex, int pageSize) {
        return foodItemRepository.findAll(pageIndex, pageSize).stream()
                .map(mapper::toResult)
                .toList();
    }

    @Override
    public Optional<FoodItemDetailsResult> findById(Integer id) {
        return foodItemRepository.findById(id).map(mapper::toDetails);
    }

    @Override
    public FoodItemResult create(CreateFoodItemCommand command) {
        FoodItem foodItem = FoodItem.create(command.name(), command.category(), command.servingSizeG(),
                command.tags(), command.ownerProfileId(), mapper.fromInputsToDomain(command.nutrients()));
        return mapper.toResult(foodItemRepository.save(foodItem));
    }

    @Override
    public FoodItemResult update(UpdateFoodItemCommand command) {
        FoodItem foodItem = foodItemRepository.findById(command.id()).orElseThrow();
        foodItem.setName(command.name());
        foodItem.setCategory(command.category());
        foodItem.setServingSizeG(command.servingSizeG());
        foodItem.setTags(command.tags());
        foodItem.replaceNutrients(mapper.fromInputsToDomain(command.nutrients()));
        return mapper.toResult(foodItemRepository.save(foodItem));
    }

    @Override
    public void deleteById(Integer id) {
        foodItemRepository.deleteById(id);
    }
}
