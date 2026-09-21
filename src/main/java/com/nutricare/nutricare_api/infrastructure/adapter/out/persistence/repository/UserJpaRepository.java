package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.model.UserJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Integer> {
    Optional<UserJpaEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
