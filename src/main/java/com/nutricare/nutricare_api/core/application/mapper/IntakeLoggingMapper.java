package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogResult;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;

import java.util.List;
import java.util.stream.Collectors;

public class IntakeLoggingMapper {
    public IntakeLogResult toLogResult(IntakeLog intakeLog) {
        return new IntakeLogResult(intakeLog.getId(), intakeLog.getLogDate());
    }

    public List<IntakeLogResult> toListLogsResult(List<IntakeLog> intakeLogs) {
        return intakeLogs.stream()
                .map(this::toLogResult)
                .collect(Collectors.toList());
    }

    public IntakeEntryResult toEntryResult(IntakeEntry entry) {
        return new IntakeEntryResult(entry.getId(), entry.getFoodItemId(), entry.getQuantityG(), entry.getMealSlot());
    }

    public List<IntakeEntryResult> toListEntriesResult(List<IntakeEntry> entries) {
        return entries.stream()
                .map(this::toEntryResult)
                .collect(Collectors.toList());
    }
}
