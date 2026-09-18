package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.intake.IntakeEntry;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.IntakeEntryJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface IntakeEntryPersistenceMapper {
    default IntakeEntry toDomain(IntakeEntryJpaEntity entity) {
        return IntakeEntry.reconstitute(entity.getId(), entity.getFoodItemId(), entity.getQuantityG(), entity.getMealSlot());
    }

    IntakeEntryJpaEntity toEntity(IntakeEntry domain);
}
