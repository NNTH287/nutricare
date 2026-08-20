package com.nutricare.nutricare_api.core.domain.entity.fooditem;

public class InvalidFoodItemException extends RuntimeException {
    public InvalidFoodItemException(String message) {
        super(message);
    }
}
