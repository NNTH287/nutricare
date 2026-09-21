package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "calculation_result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class CalculationResultJpaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @Column(name = "standard_id", nullable = false)
    private Integer standardId;

    @Column(name = "calculated_at", nullable = false)
    private LocalDateTime calculatedAt;

    @Column(name = "calorie_target", nullable = false)
    private Double calorieTarget;

    @ElementCollection
    @CollectionTable(name = "calculation_result_nutrient_target", joinColumns = @JoinColumn(name = "calculation_result_id"))
    private Set<NutrientTargetEmbeddable> nutrientTargets = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "calculation_result_unresolved_nutrient", joinColumns = @JoinColumn(name = "calculation_result_id"))
    @Column(name = "nutrient_id")
    private Set<Integer> unresolvedNutrientIds = new HashSet<>();
}
