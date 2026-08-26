package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.UpdateNutrientCommand;
import com.nutricare.nutricare_api.core.application.mapper.NutrientMapper;
import com.nutricare.nutricare_api.core.application.port.in.ManageNutrientUseCase;
import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.InvalidNutrientException;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;

import java.util.Optional;

public class ManageNutrientService implements ManageNutrientUseCase {
    private final NutrientRepository nutrientRepository;
    private final NutrientMapper mapper;

    public ManageNutrientService(NutrientRepository nutrientRepository, NutrientMapper mapper) {
        this.nutrientRepository = nutrientRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Nutrient> findById(Integer id) {
        return nutrientRepository.findById(id);
    }

    @Override
    public Integer save(CreateNutrientCommand command) {
        if(nutrientRepository.existsCode(command.code())) {
            throw new InvalidNutrientException("code is existed");
        }

        return nutrientRepository.save(mapper.fromCreateCommandToDomain(command)).getId();
    }

    @Override
    public Nutrient update(UpdateNutrientCommand command) {
        Nutrient updatedEntity = nutrientRepository.findById(command.id()).orElseThrow();

        if (!command.code().equals(updatedEntity.getCode()) && nutrientRepository.existsCode(command.code())) {
            throw new InvalidNutrientException("code already exists");
        }

        updatedEntity.setCode(command.code());
        updatedEntity.setName(command.name());
        updatedEntity.setUnit(command.unit());
        updatedEntity = nutrientRepository.save(updatedEntity);

        return updatedEntity;
    }

    @Override
    public int deleteById(Integer id) {
        return nutrientRepository.deleteById(id);
    }
}
