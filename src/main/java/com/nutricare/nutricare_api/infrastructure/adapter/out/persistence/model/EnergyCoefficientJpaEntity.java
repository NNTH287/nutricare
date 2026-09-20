package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import com.nutricare.nutricare_api.core.domain.entity.profile.GroupType;
import com.nutricare.nutricare_api.core.domain.entity.profile.SexType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "energy_coefficient")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class EnergyCoefficientJpaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "standard_id", nullable = false)
    private Integer standardId;

    @Enumerated(EnumType.STRING)
    @Column(name = "group_type", nullable = false)
    private GroupType groupType;

    @Enumerated(EnumType.STRING)
    @Column(name = "sex_type", nullable = false)
    private SexType sexType;

    @Column(name = "age_months_min", nullable = false)
    private Integer ageMonthsMin;

    @Column(name = "age_months_max", nullable = false)
    private Integer ageMonthsMax;

    @Column(name = "trimester")
    private Integer trimester;

    @Column(name = "weight_coefficient", nullable = false)
    private double weightCoefficient;

    @Column(name = "height_coefficient", nullable = false)
    private double heightCoefficient;

    @Column(name = "intercept", nullable = false)
    private double intercept;
}
