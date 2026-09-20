package com.nutricare.nutricare_api.core.application.port.out;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLogHeader;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface IntakeLogRepository {
    List<IntakeLogHeader> findAllByProfileId(Integer profileId, int pageIndex, int pageSize);

    Optional<IntakeLog> findById(Integer id);

    Optional<IntakeLog> findByProfileIdAndDate(Integer profileId, LocalDate date);

    IntakeLog save(IntakeLog intakeLog);

    void deleteById(Integer id);

    List<IntakeEntry> findAllEntriesByProfileIdAndDate(Integer profileId, LocalDate date);

    Optional<IntakeEntry> findEntryById(Integer id);
}
