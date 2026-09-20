package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.ProfileRepository;
import com.nutricare.nutricare_api.core.domain.entity.profile.Profile;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.ProfilePersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class ProfileRepositoryAdapter implements ProfileRepository {
    private final ProfileJpaRepository repository;
    private final ProfilePersistenceMapper mapper;

    @Override
    public Profile getById(Integer id) {
        return repository.findById(id).map(mapper::toDomain).orElseThrow();
    }
}
