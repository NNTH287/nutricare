package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.RefreshTokenRepository;
import com.nutricare.nutricare_api.core.domain.entity.user.RefreshToken;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.RefreshTokenPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryAdapter implements RefreshTokenRepository {
    private final RefreshTokenJpaRepository repository;
    private final RefreshTokenPersistenceMapper mapper;

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        return mapper.toDomain(repository.save(mapper.toEntity(refreshToken)));
    }

    @Override
    public Optional<RefreshToken> findByJti(UUID jti) {
        return repository.findById(jti).map(mapper::toDomain);
    }

    @Override
    public List<RefreshToken> findActiveByUserId(Integer userId) {
        return repository.findByUserIdAndRevokedAtIsNull(userId).stream().map(mapper::toDomain).toList();
    }
}
