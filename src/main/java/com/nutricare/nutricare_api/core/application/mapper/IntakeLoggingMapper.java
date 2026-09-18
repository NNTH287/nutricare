package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogHeaderResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogResult;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLogHeader;

import java.util.List;
import java.util.stream.Collectors;

public class IntakeLoggingMapper {
    public IntakeLogResult toLogResult(IntakeLog intakeLog) {
        return new IntakeLogResult(intakeLog.getId(), intakeLog.getLogDate());
    }

    public List<IntakeLogResult> toListLogsResult(List<IntakeLog> intakeLogs) {
        return intakeLogs.stream()
                .map(this::toLogResult)
                .toList();
    }

    public IntakeEntryResult toEntryResult(IntakeEntry entry) {
        return new IntakeEntryResult(entry.getId(), entry.getFoodItemId(), entry.getQuantityG(), entry.getMealSlot());
    }

    public List<IntakeEntryResult> toListEntriesResult(List<IntakeEntry> entries) {
        return entries.stream()
                .map(this::toEntryResult)
                .toList();
    }

    public IntakeLogHeaderResult toLogsHeaderResult(IntakeLogHeader header) {
        return new IntakeLogHeaderResult(header.id(), header.profileId(), header.logDate());

    }

    public List<IntakeLogHeaderResult> toListLogsHeaderResult(List<IntakeLogHeader> headers) {
        return headers.stream()
                .map(this::toLogsHeaderResult)
                .toList();
    }
}
