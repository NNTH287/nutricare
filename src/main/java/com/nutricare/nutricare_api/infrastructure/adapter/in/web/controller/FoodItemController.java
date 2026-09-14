package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.dto.FoodItemDetailsResult;
import com.nutricare.nutricare_api.core.application.port.in.ManageFoodItemUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateFoodItemRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.FoodItemDetailsResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.FoodItemResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateFoodItemRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.FoodItemWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodItemController {
    private final ManageFoodItemUseCase useCase;
    private final FoodItemWebMapper mapper;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<FoodItemResponse>>> listFoodItem(@RequestParam Integer pageIndex, @RequestParam Integer pageSize) {
        return ApiResponse.ok(mapper.toResponseList(useCase.getFoodItems(pageIndex, pageSize)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemDetailsResponse>> getFoodItem(@PathVariable Integer id) {
        return ApiResponse.ok(mapper.toDetailsResponse(useCase.findById(id).orElseThrow()));
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<FoodItemDetailsResponse>> createFoodItem(@RequestBody CreateFoodItemRequest req) {
        FoodItemDetailsResult createdFoodItem = useCase.create(mapper.toCreateCommand(req));
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(createdFoodItem.id())
                .toUri();

        return ApiResponse.created(location, mapper.toDetailsResponse(createdFoodItem));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FoodItemDetailsResponse>> updateFoodItem(@PathVariable Integer id, @RequestBody UpdateFoodItemRequest req) {
        return ApiResponse.ok(mapper.toDetailsResponse(useCase.update(mapper.toUpdateCommand(id, req))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodItem(@PathVariable Integer id) {
        useCase.deleteById(id);
        return ApiResponse.noContent();
    }
}
