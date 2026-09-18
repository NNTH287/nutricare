package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLog;
import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeLogHeader;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeEntryJpaEntity;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeLogJpaEntity;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring", uses = IntakeEntryPersistenceMapper.class)
public interface IntakeLogPersistenceMapper {
    default IntakeLog toDomain(IntakeLogJpaEntity entity) {
        return IntakeLog.reconstitute(
                entity.getId(), entity.getProfileId(), entity.getLogDate(),
                toDomainEntries(entity.getEntries())
        );
    }
    IntakeLogJpaEntity toEntity(IntakeLog domain);

    List<IntakeEntry> toDomainEntries(List<IntakeEntryJpaEntity> entities);

    IntakeLogHeader toDomainHeader(IntakeLogJpaEntity entity);

    @AfterMapping
    default void linkEntriesToParent(@MappingTarget IntakeLogJpaEntity entity) {
        entity.getEntries().forEach(entry -> entry.setIntakeLog(entity));
    }
}
