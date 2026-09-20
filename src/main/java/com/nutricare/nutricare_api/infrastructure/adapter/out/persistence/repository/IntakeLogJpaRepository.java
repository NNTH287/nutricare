package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeLogJpaEntity;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface IntakeLogJpaRepository extends JpaRepository<IntakeLogJpaEntity, Integer> {
    List<IntakeLogJpaEntity> findAllHeaderByProfileId(Integer profileId, Pageable pageable);

    Optional<IntakeLogJpaEntity> findByProfileIdAndLogDate(Integer profileId, LocalDate logDate);
}
