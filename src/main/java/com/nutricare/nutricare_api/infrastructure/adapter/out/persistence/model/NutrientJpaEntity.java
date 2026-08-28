package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nutrient")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class NutrientJpaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "unit", nullable = false)
    private String unit;
}
