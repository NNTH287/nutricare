package com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.repository;

import com.nutricare.nutricare_api.core.application.port.out.FoodItemRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import com.nutricare.nutricare_api.infrastructure.adapter.out.persistence.mapper.FoodItemPersistenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class FoodItemRepositoryAdapter implements FoodItemRepository {
    private final FoodItemJpaRepository repository;
    private final FoodItemPersistenceMapper mapper;

    @Override
    public List<FoodItem> findAll(int pageIndex, int pageSize) {
        return repository.findAll(PageRequest.of(pageIndex, pageSize)).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<FoodItem> findById(Integer id) {
        return repository.findById(id).map(mapper::toDomain);
    }

    @Override
    public FoodItem save(FoodItem foodItem) {
        return mapper.toDomain(repository.save(mapper.toEntity(foodItem)));
    }

    @Override
    public void deleteById(Integer id) {
        repository.deleteById(id);
    }
}
