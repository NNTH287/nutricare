package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nutrient_requirement")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NutrientRequirementJpaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "standard_id", nullable = false)
    private Integer standardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false)
    private GroupType groupType;

    @Column(name = "nutrient_id", nullable = false)
    private Integer nutrientId;

    @Column(name = "age_months_min", nullable = false)
    private Integer ageMonthsMin;

    @Column(name = "age_months_max", nullable = false)
    private Integer ageMonthsMax;

    @Column(name = "trimester")
    private Integer trimester;

    @Column(name = "recommended_val", nullable = false)
    private Double recommendedVal;

    @Column(name = "max_val")
    private Double maxVal;
}
