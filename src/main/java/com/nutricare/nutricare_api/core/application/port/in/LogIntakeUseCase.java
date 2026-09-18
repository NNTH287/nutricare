package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LogIntakeUseCase {
    List<IntakeLogHeaderResult> listLogs(Integer profileId, int pageIndex, int pageSize);
    IntakeLogHeaderResult createLog(Integer profileId);
    void deleteLogById(Integer id);
    List<IntakeEntryResult> listEntriesOnDate(Integer profileId, LocalDate date);
    Optional<IntakeEntryResult> findEntryById(Integer id);
    IntakeEntryResult createEntry(CreateIntakeEntryCommand command);
    IntakeEntryResult updateEntry(UpdateIntakeEntryCommand command);
    void deleteEntryById(Integer intakeLogId, Integer entryId);
}
