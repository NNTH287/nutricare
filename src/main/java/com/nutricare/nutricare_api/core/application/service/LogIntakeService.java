package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateIntakeEntryCommand;
import com.nutricare.nutricare_api.core.application.mapper.IntakeLoggingMapper;
import com.nutricare.nutricare_api.core.application.port.in.LogIntakeUseCase;
import com.nutricare.nutricare_api.core.application.port.out.IntakeLogRepository;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LogIntakeService implements LogIntakeUseCase {
    private final IntakeLogRepository intakeLogRepository;
    private final IntakeLoggingMapper mapper;

    public LogIntakeService(IntakeLogRepository intakeLogRepository, IntakeLoggingMapper mapper) {
        this.intakeLogRepository = intakeLogRepository;
        this.mapper = mapper;
    }

    @Override
    public List<IntakeLogResult> listLogs(Integer profileId, int pageIndex, int pageSize) {
        return mapper.toListLogsResult(intakeLogRepository.findAllByProfileId(profileId, pageIndex, pageSize));
    }

    @Override
    public Optional<IntakeLogResult> findLogById(Integer id) {
        return intakeLogRepository.findById(id).map(mapper::toLogResult);
    }

    @Override
    public IntakeLogResult createLog(Integer profileId) {
        LocalDate today = LocalDate.now();
        Optional<IntakeLog> intakeLog = intakeLogRepository.findByProfileIdAndDate(profileId, today);

        if(intakeLog.isPresent()) {
            return mapper.toLogResult(intakeLog.get());
        } else {
            return mapper.toLogResult(IntakeLog.create(profileId, today));
        }
    }

    @Override
    public void deleteLogById(Integer id) {
        intakeLogRepository.deleteById(id);
    }

    @Override
    public List<IntakeEntryResult> listEntriesOnDate(Integer profileId, LocalDate date) {
        return mapper.toListEntriesResult(intakeLogRepository.findAllEntriesByProfileIdAndDate(profileId, date));
    }

    @Override
    public Optional<IntakeEntryResult> findEntryById(Integer id) {
        return intakeLogRepository.findEntryById(id).map(mapper::toEntryResult);
    }

    @Override
    public IntakeEntryResult createEntry(CreateIntakeEntryCommand command) {
        IntakeLog intakeLog = intakeLogRepository.findById(command.intakeLogId()).orElseThrow();
        IntakeEntry newEntry = intakeLog.addEntry(command.foodItemId(), command.quantityG(), command.mealSlot());
        intakeLogRepository.save(intakeLog);

        return mapper.toEntryResult(newEntry);
    }

    @Override
    public IntakeEntryResult updateEntry(UpdateIntakeEntryCommand command) {
        IntakeLog intakeLog = intakeLogRepository.findById(command.intakeLogId()).orElseThrow();
        intakeLog.updateEntryQuantity(command.entryId(), command.quantityG());
        intakeLog.updateEntryMealSlot(command.entryId(), command.mealSlot());
        intakeLogRepository.save(intakeLog);

        return mapper.toEntryResult(intakeLog.getEntry(command.entryId()));
    }

    @Override
    public void deleteEntryById(Integer intakeLogId, Integer entryId) {
        IntakeLog intakeLog = intakeLogRepository.findById(intakeLogId).orElseThrow();
        intakeLog.removeEntry(entryId);
        intakeLogRepository.save(intakeLog);
    }
}
