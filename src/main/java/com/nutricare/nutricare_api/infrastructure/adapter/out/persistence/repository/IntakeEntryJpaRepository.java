package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeEntryJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IntakeEntryJpaRepository extends JpaRepository<IntakeEntryJpaEntity, Integer> {
}
