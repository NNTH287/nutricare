package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "intake_entry")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class IntakeEntryJpaEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "intake_log_id", nullable = false)
    private IntakeLogJpaEntity intakeLog;

    @Column(name = "food_item_id", nullable = false)
    private Integer foodItemId;

    @Column(name = "quantity_g", nullable = false)
    private Double quantityG;

    @Enumerated(EnumType.STRING)
    @Column(name = "meal_slot", nullable = false)
    private MealSlot mealSlot;
}
