package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food_item")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class FoodItemJpaEntity {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "category")
    private String category;

    @Column(name = "serving_size_g", nullable = false)
    private Double servingSizeG;

    @Column(name = "owner_profile_id")
    private Integer ownerProfileId;

    @ElementCollection
    @CollectionTable(name = "food_item_tag", joinColumns = @JoinColumn(name = "food_item_id"))
    @Column(name = "tag")
    private Set<String> tags = new HashSet<>();

    @ElementCollection
    @CollectionTable(name = "food_item_nutrient", joinColumns = @JoinColumn(name = "food_item_id"))
    private Set<FoodNutrientEmbeddable> nutrients = new HashSet<>();
}
