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
public class NutrientTargetEmbeddable {
    @Column(name = "nutrient_id", nullable = false)
    private Integer nutrientId;

    @Column(name = "target_amount_g", nullable = false)
    private Double targetAmountG;

    @Column(name = "max_amount_g")
    private Double maxAmountG;
}
