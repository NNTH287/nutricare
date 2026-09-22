package com.nutricare.nutricare_api.core.application.service;

import com.nutricare.nutricare_api.core.application.dto.CreateFoodItemCommand;
import com.nutricare.nutricare_api.core.application.dto.FoodItemDetailsResult;
import com.nutricare.nutricare_api.core.application.dto.FoodItemResult;
import com.nutricare.nutricare_api.core.application.dto.UpdateFoodItemCommand;
import com.nutricare.nutricare_api.core.application.mapper.FoodItemMapper;
import com.nutricare.nutricare_api.core.application.port.out.FoodItemRepository;
import com.nutricare.nutricare_api.core.domain.entity.fooditem.FoodItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ManageFoodItemServiceTest {

    @Mock
    private FoodItemRepository foodItemRepository;

    private ManageFoodItemService service;

    @BeforeEach
    void setUp() {
        service = new ManageFoodItemService(foodItemRepository, new FoodItemMapper());
    }

    @Test
    void givenRepositoryReturnsItems_whenListIsCalled_thenReturnsMappedResults() {
        FoodItem rice = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());
        when(foodItemRepository.findAll(0, 10)).thenReturn(List.of(rice));

        List<FoodItemResult> results = service.list(0, 10);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).name()).isEqualTo("Rice");
    }

    @Test
    void givenExistingId_whenFindByIdIsCalled_thenReturnsMappedDetails() {
        FoodItem rice = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());
        when(foodItemRepository.findById(1)).thenReturn(Optional.of(rice));

        Optional<FoodItemDetailsResult> result = service.findById(1);

        assertThat(result).isPresent();
        assertThat(result.get().name()).isEqualTo("Rice");
    }

    @Test
    void givenUnknownId_whenFindByIdIsCalled_thenReturnsEmpty() {
        when(foodItemRepository.findById(999)).thenReturn(Optional.empty());

        Optional<FoodItemDetailsResult> result = service.findById(999);

        assertThat(result).isEmpty();
    }

    @Test
    void givenValidCommand_whenCreateIsCalled_thenBuildsDomainItemAndPersistsIt() {
        CreateFoodItemCommand command = new CreateFoodItemCommand("Rice", "Grain", 100.0, Set.of(), null, Set.of());
        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FoodItemDetailsResult result = service.create(command);

        assertThat(result.name()).isEqualTo("Rice");
        verify(foodItemRepository).save(any(FoodItem.class));
    }

    @Test
    void givenUnknownId_whenUpdateIsCalled_thenThrowsNoSuchElementException() {
        UpdateFoodItemCommand command = new UpdateFoodItemCommand(999, "Rice", "Grain", 100.0, Set.of(), Set.of());
        when(foodItemRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(command))
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void givenExistingIdAndValidChanges_whenUpdateIsCalled_thenAppliesChangesAndPersists() {
        FoodItem existing = FoodItem.create("Rice", "Grain", 100.0, Set.of(), null, Set.of());
        UpdateFoodItemCommand command = new UpdateFoodItemCommand(existing.getId(), "Brown Rice", "Grain",
                120.0, Set.of("whole-grain"), Set.of());
        when(foodItemRepository.findById(existing.getId())).thenReturn(Optional.of(existing));
        when(foodItemRepository.save(any(FoodItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        FoodItemDetailsResult result = service.update(command);

        assertThat(result.name()).isEqualTo("Brown Rice");
        assertThat(result.servingSizeG()).isEqualTo(120.0);
        assertThat(result.tags()).containsExactly("whole-grain");
    }

    @Test
    void givenExistingId_whenDeleteByIdIsCalled_thenDelegatesToRepository() {
        service.deleteById(1);

        verify(foodItemRepository).deleteById(1);
    }
}
