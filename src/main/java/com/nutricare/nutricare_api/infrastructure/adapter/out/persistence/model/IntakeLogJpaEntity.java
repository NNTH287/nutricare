package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "intake_log")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class IntakeLogJpaEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "profile_id", nullable = false)
    private Integer profileId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "intakeLog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<IntakeEntryJpaEntity> entries = new ArrayList<>();
}
