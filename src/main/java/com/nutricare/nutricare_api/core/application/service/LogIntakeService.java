package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.*;
import com.nutricare.nutricare_api.core.application.mapper.IntakeLoggingMapper;
import com.nutricare.nutricare_api.core.application.port.in.LogIntakeUseCase;
import com.nutricare.nutricare_api.core.application.port.out.IntakeLogRepository;
import com.nutricare.nutricare_api.core.application.port.out.ProfileRepository;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LogIntakeService implements LogIntakeUseCase {
    private final IntakeLogRepository intakeLogRepository;
    private final ProfileRepository profileRepository;
    private final IntakeLoggingMapper mapper;

    public LogIntakeService(IntakeLogRepository intakeLogRepository, ProfileRepository profileRepository, IntakeLoggingMapper mapper) {
        this.intakeLogRepository = intakeLogRepository;
        this.profileRepository = profileRepository;
        this.mapper = mapper;
    }

    @Override
    public List<IntakeLogHeaderResult> listLogs(Integer profileId, int pageIndex, int pageSize, Integer authenticatedUserId) {
        verifyProfileOwnedBy(profileId, authenticatedUserId);
        return mapper.toListLogHeaderResult(intakeLogRepository.findAllByProfileId(profileId, pageIndex, pageSize));
    }

    @Override
    public IntakeLogHeaderResult createLog(Integer profileId, LocalDate logDate, Integer authenticatedUserId) {
        verifyProfileOwnedBy(profileId, authenticatedUserId);
        Optional<IntakeLog> intakeLog = intakeLogRepository.findByProfileIdAndDate(profileId, logDate);

        if(intakeLog.isPresent()) {
            return mapper.toLogHeaderResult(intakeLog.get());
        } else {
            IntakeLog newLog = IntakeLog.create(profileId, logDate);
            return mapper.toLogHeaderResult(intakeLogRepository.save(newLog));
        }
    }

    @Override
    public void deleteLogById(Integer id, Integer authenticatedUserId) {
        IntakeLog intakeLog = intakeLogRepository.findById(id).orElseThrow();
        verifyProfileOwnedBy(intakeLog.getProfileId(), authenticatedUserId);
        intakeLogRepository.deleteById(id);
    }

    @Override
    public List<IntakeEntryResult> listEntriesOnDate(Integer profileId, LocalDate date, Integer authenticatedUserId) {
        verifyProfileOwnedBy(profileId, authenticatedUserId);
        return mapper.toListEntriesResult(intakeLogRepository.findAllEntriesByProfileIdAndDate(profileId, date));
    }

    @Override
    public Optional<IntakeEntryResult> findEntryById(Integer id) {
        return intakeLogRepository.findEntryById(id).map(mapper::toEntryResult);
    }

    @Override
    public IntakeEntryResult createEntry(CreateIntakeEntryCommand command, Integer authenticatedUserId) {
        IntakeLog intakeLog = intakeLogRepository.findById(command.intakeLogId()).orElseThrow();
        verifyProfileOwnedBy(intakeLog.getProfileId(), authenticatedUserId);
        IntakeEntry newEntry = intakeLog.addEntry(command.foodItemId(), command.quantityG(), command.mealSlot());
        intakeLogRepository.save(intakeLog);

        return mapper.toEntryResult(newEntry);
    }

    @Override
    public IntakeEntryResult updateEntry(UpdateIntakeEntryCommand command, Integer authenticatedUserId) {
        IntakeLog intakeLog = intakeLogRepository.findById(command.intakeLogId()).orElseThrow();
        verifyProfileOwnedBy(intakeLog.getProfileId(), authenticatedUserId);
        intakeLog.updateEntryQuantity(command.entryId(), command.quantityG());
        intakeLog.updateEntryMealSlot(command.entryId(), command.mealSlot());
        intakeLogRepository.save(intakeLog);

        return mapper.toEntryResult(intakeLog.getEntry(command.entryId()));
    }

    @Override
    public void deleteEntryById(Integer intakeLogId, Integer entryId, Integer authenticatedUserId) {
        IntakeLog intakeLog = intakeLogRepository.findById(intakeLogId).orElseThrow();
        verifyProfileOwnedBy(intakeLog.getProfileId(), authenticatedUserId);
        intakeLog.removeEntry(entryId);
        intakeLogRepository.save(intakeLog);
    }

    private void verifyProfileOwnedBy(Integer profileId, Integer authenticatedUserId) {
        Profile profile = profileRepository.getById(profileId);
        profile.verifyOwnedBy(authenticatedUserId);
    }
}
