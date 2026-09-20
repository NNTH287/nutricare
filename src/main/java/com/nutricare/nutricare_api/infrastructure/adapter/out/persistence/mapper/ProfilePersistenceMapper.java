package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.ProfileJpaEntity;
import org.mapstruct.Mapper;

import java.util.HashSet;

@Mapper(componentModel = "spring")
public interface ProfilePersistenceMapper {
    default Profile toDomain(ProfileJpaEntity entity) {
        return Profile.reconstitute(entity.getId(), entity.getUserId(), entity.getName(), entity.getGroupType(),
                entity.getSexType(), entity.getBirthDate(), entity.getWeightKg(), entity.getHeightCm(),
                entity.getActivityLevel(), entity.getTrimester(), entity.getConditions(), entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    default ProfileJpaEntity toEntity(Profile domain) {
        ProfileJpaEntity entity = new ProfileJpaEntity();
        entity.setId(domain.getId());
        entity.setUserId(domain.getUserId());
        entity.setName(domain.getName());
        entity.setGroupType(domain.getGroupType());
        entity.setSexType(domain.getSexType());
        entity.setBirthDate(domain.getBirthDate());
        entity.setWeightKg(domain.getWeightKg());
        entity.setHeightCm(domain.getHeightCm());
        entity.setActivityLevel(domain.getActivityLevel());
        entity.setTrimester(domain.getTrimester());
        entity.setConditions(new HashSet<>(domain.getConditions()));
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
