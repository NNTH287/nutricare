package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LogIntakeUseCase {
    List<IntakeLogHeaderResult> listLogs(Integer profileId, int pageIndex, int pageSize, Integer authenticatedUserId);
    IntakeLogHeaderResult createLog(Integer profileId, LocalDate logDate, Integer authenticatedUserId);
    void deleteLogById(Integer id, Integer authenticatedUserId);
    List<IntakeEntryResult> listEntriesOnDate(Integer profileId, LocalDate date, Integer authenticatedUserId);
    Optional<IntakeEntryResult> findEntryById(Integer id);
    IntakeEntryResult createEntry(CreateIntakeEntryCommand command, Integer authenticatedUserId);
    IntakeEntryResult updateEntry(UpdateIntakeEntryCommand command, Integer authenticatedUserId);
    void deleteEntryById(Integer intakeLogId, Integer entryId, Integer authenticatedUserId);
}
