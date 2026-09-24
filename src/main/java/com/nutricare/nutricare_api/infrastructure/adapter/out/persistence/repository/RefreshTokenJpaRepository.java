package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.RefreshTokenJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenJpaEntity, UUID> {
    List<RefreshTokenJpaEntity> findByUserIdAndRevokedAtIsNull(Integer userId);

    void deleteByExpiresAtBefore(LocalDateTime cutoff);
}
