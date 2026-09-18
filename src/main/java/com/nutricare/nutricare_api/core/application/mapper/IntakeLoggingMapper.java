package com.nutricare.nutricare_api.core.application.mapper;

import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogHeaderResult;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLogHeader;

import java.util.List;

public class IntakeLoggingMapper {

    public IntakeEntryResult toEntryResult(IntakeEntry entry) {
        return new IntakeEntryResult(entry.getId(), entry.getFoodItemId(), entry.getQuantityG(), entry.getMealSlot());
    }

    public List<IntakeEntryResult> toListEntriesResult(List<IntakeEntry> entries) {
        return entries.stream()
                .map(this::toEntryResult)
                .toList();
    }

    public IntakeLogHeaderResult toLogHeaderResult(IntakeLogHeader header) {
        return new IntakeLogHeaderResult(header.id(), header.profileId(), header.logDate());
    }

    public IntakeLogHeaderResult toLogHeaderResult(IntakeLog intakeLog) {
        return new IntakeLogHeaderResult(intakeLog.getId(), intakeLog.getProfileId(), intakeLog.getLogDate());
    }

    public List<IntakeLogHeaderResult> toListLogHeaderResult(List<IntakeLogHeader> headers) {
        return headers.stream()
                .map(this::toLogHeaderResult)
                .toList();
    }
}
