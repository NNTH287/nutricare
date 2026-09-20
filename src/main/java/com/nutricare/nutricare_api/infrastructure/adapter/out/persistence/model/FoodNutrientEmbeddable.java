package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FoodNutrientEmbeddable {
    @Column(name = "nutrient_id", nullable = false)
    private Integer nutrientId;

    @Column(name = "amount_per_100g", nullable = false)
    private Double amountPer100g;
}
