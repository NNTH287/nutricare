package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateNutrientCommand;
import com.nutricare.nutricare_api.core.application.dto.NutrientResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateNutrientCommand;
import com.nutricare.nutricare_api.core.application.mapper.NutrientMapper;
import com.nutricare.nutricare_api.core.application.port.out.NutrientRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.InvalidNutrientException;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.Nutrient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ManageNutrientServiceTest {

    @Mock
    private NutrientRepository nutrientRepository;

    private ManageNutrientService service;

    @BeforeEach
    void setUp() {
        service = new ManageNutrientService(nutrientRepository, new NutrientMapper());
    }

    @Test
    void givenExistingId_whenFindByIdIsCalled_thenReturnsMappedResult() {
        when(nutrientRepository.findById(1)).thenReturn(Optional.of(Nutrient.reconstitute(1, "protein", "Protein", "g")));

        Optional<NutrientResult> result = service.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().code()).isEqualTo("protein");
    }

    @Test
    void givenUnknownId_whenFindByIdIsCalled_thenReturnsEmpty() {
        when(nutrientRepository.findById(999)).thenReturn(Optional.empty());

        assertThat(service.findById(999)).isEmpty();
    }

    @Test
    void givenCodeAlreadyExists_whenCreateIsCalled_thenThrowsInvalidNutrientExceptionAndNeverSaves() {
        when(nutrientRepository.existsCode("protein")).thenReturn(true);

        assertThatThrownBy(() -> service.create(new CreateNutrientCommand("protein", "Protein", "g")))
                .isInstanceOf(InvalidNutrientException.class);

        verify(nutrientRepository, never()).save(any());
    }

    @Test
    void givenNewCode_whenCreateIsCalled_thenBuildsDomainNutrientAndPersistsIt() {
        when(nutrientRepository.existsCode("protein")).thenReturn(false);
        when(nutrientRepository.save(any(Nutrient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NutrientResult result = service.create(new CreateNutrientCommand("protein", "Protein", "g"));

        assertThat(result.code()).isEqualTo("protein");
        verify(nutrientRepository).save(any(Nutrient.class));
    }

    @Test
    void givenUnknownId_whenUpdateIsCalled_thenThrowsNoSuchElementException() {
        when(nutrientRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(new UpdateNutrientCommand(999, "protein", "Protein", "g")))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void givenNewCodeAlreadyUsedByAnotherNutrient_whenUpdateIsCalled_thenThrowsInvalidNutrientExceptionAndNeverSaves() {
        Nutrient existing = Nutrient.reconstitute(1, "protein", "Protein", "g");
        when(nutrientRepository.findById(1)).thenReturn(Optional.of(existing));
        when(nutrientRepository.existsCode("fat")).thenReturn(true);

        assertThatThrownBy(() -> service.update(new UpdateNutrientCommand(1, "fat", "Fat", "g")))
                .isInstanceOf(InvalidNutrientException.class);

        verify(nutrientRepository, never()).save(any());
    }

    @Test
    void givenSameCodeAsBefore_whenUpdateIsCalled_thenDoesNotTreatItAsADuplicate() {
        Nutrient existing = Nutrient.reconstitute(1, "protein", "Protein", "g");
        when(nutrientRepository.findById(1)).thenReturn(Optional.of(existing));
        when(nutrientRepository.save(any(Nutrient.class))).thenAnswer(invocation -> invocation.getArgument(0));

        NutrientResult result = service.update(new UpdateNutrientCommand(1, "protein", "Protein (updated)", "g"));

        assertThat(result.name()).isEqualTo("Protein (updated)");
        verify(nutrientRepository, never()).existsCode(any());
    }

    @Test
    void givenExistingId_whenDeleteByIdIsCalled_thenDelegatesToRepository() {
        service.deleteById(1);

        verify(nutrientRepository).deleteById(1);
    }
}
