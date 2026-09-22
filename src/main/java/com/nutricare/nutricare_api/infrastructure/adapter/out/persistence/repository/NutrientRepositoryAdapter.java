package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.NutrientPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class NutrientRepositoryAdapter implements NutrientRepository {
    private final NutrientJpaRepository repository;
    private final NutrientPersistenceMapper mapper;

    @Override
    public Optional<Nutrient> findById(Integer nutrientId) {
        return repository.findById(nutrientId).map(mapper::toDomain);
    }

    @Override
    public Optional<Nutrient> findByCode(String code) {
        return repository.findByCode(code).map(mapper::toDomain);
    }

    @Override
    public boolean existsCode(String code) {
        return repository.existsByCode(code);
    }

    @Override
    public Nutrient save(Nutrient nutrient) {
        return mapper.toDomain(repository.save(mapper.toEntity(nutrient)));
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
