package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.IntakeLogRepository;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLogHeader;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.IntakeEntryPersistenceMapper;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.IntakeLogPersistenceMapper;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeLogJpaEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class IntakeLogRepositoryAdapter implements IntakeLogRepository {
    private final IntakeLogJpaRepository intakeLogRepo;
    private final IntakeEntryJpaRepository entryRepo;
    private final IntakeLogPersistenceMapper intakeLogMapper;
    private final IntakeEntryPersistenceMapper entryMapper;

    @Override
    public List<IntakeLogHeader> findAllByProfileId(Integer profileId, int pageIndex, int pageSize) {
        return intakeLogRepo.findAllHeaderByProfileId(profileId, PageRequest.of(pageIndex, pageSize)).stream()
                .map(intakeLogMapper::toDomainHeader)
                .toList();
    }

    @Override
    public Optional<IntakeLog> findById(Integer id) {
        return intakeLogRepo.findById(id).map(intakeLogMapper::toDomain);
    }

    @Override
    public Optional<IntakeLog> findByProfileIdAndDate(Integer profileId, LocalDate date) {
        return intakeLogRepo.findByProfileIdAndLogDate(profileId, date).map(intakeLogMapper::toDomain);
    }

    @Override
    public IntakeLog save(IntakeLog intakeLog) {
        return intakeLogMapper.toDomain(intakeLogRepo.save(intakeLogMapper.toEntity(intakeLog)));
    }

    @Override
    public void deleteById(Integer id) {
        intakeLogRepo.deleteById(id);
    }

    @Override
    public List<IntakeEntry> findAllEntriesByProfileIdAndDate(Integer profileId, LocalDate date) {
        return intakeLogRepo.findByProfileIdAndLogDate(profileId, date)
                .map(IntakeLogJpaEntity::getEntries)
                .map(entries -> entries.stream().map(entryMapper::toDomain).toList())
                .orElse(List.of());
    }

    @Override
    public Optional<IntakeEntry> findEntryById(Integer id) {
        return entryRepo.findById(id).map(entryMapper::toDomain);
    }
}
