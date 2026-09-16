package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.CreateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateIntakeEntryCommand;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LogIntakeUseCase {
    List<IntakeLogResult> listLogs(Integer profileId, int pageIndex, int pageSize);
    Optional<IntakeLogResult> findLogById(Integer id);
    IntakeLogResult createLog(Integer profileId);
    void deleteLogById(Integer id);
    List<IntakeEntryResult> listEntriesOnDate(Integer profileId, LocalDate date);
    Optional<IntakeEntryResult> findEntryById(Integer id);
    IntakeEntryResult createEntry(CreateIntakeEntryCommand command);
    IntakeEntryResult updateEntry(UpdateIntakeEntryCommand command);
    void deleteEntryById(Integer intakeLogId, Integer entryId);
}
