package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.user.User;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.UserJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserPersistenceMapper {
    default User toDomain(UserJpaEntity entity) {
        return User.reconstitute(entity.getId(), entity.getEmail(), entity.getPasswordHash(), entity.getRole(),
                entity.getCreatedAt(), entity.getUpdatedAt());
    }

    default UserJpaEntity toEntity(User domain) {
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(domain.getId());
        entity.setEmail(domain.getEmail());
        entity.setPasswordHash(domain.getPasswordHash());
        entity.setRole(domain.getRole());
        entity.setCreatedAt(domain.getCreatedAt());
        entity.setUpdatedAt(domain.getUpdatedAt());
        return entity;
    }
}
