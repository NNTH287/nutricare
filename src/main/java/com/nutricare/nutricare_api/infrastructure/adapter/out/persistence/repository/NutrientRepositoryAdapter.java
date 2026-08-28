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
        return Optional.of(mapper.toDomain(repository.findById(nutrientId).orElseThrow()));
    }

    @Override
    public boolean existsCode(String code) {
        return false;
    }

    @Override
    public Nutrient save(Nutrient nutrient) {
        return null;
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
