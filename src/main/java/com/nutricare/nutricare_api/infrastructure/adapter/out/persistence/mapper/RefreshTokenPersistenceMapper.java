package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper;

import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.RefreshTokenJpaEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefreshTokenPersistenceMapper {
    default RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        return RefreshToken.reconstitute(entity.getJti(), entity.getUserId(), entity.getIssuedAt(),
                entity.getExpiresAt(), entity.getRevokedAt(), entity.getReplacedByJti());
    }

    default RefreshTokenJpaEntity toEntity(RefreshToken domain) {
        RefreshTokenJpaEntity entity = new RefreshTokenJpaEntity();
        entity.setJti(domain.getJti());
        entity.setUserId(domain.getUserId());
        entity.setIssuedAt(domain.getIssuedAt());
        entity.setExpiresAt(domain.getExpiresAt());
        entity.setRevokedAt(domain.getRevokedAt());
        entity.setReplacedByJti(domain.getReplacedByJti());
        return entity;
    }
}
