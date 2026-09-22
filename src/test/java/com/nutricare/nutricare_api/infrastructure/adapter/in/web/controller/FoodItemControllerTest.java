package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricare.nutricare_api.core.application.dto.FoodItemDetailsResult;
import com.nutricare.nutricare_api.core.application.dto.FoodItemResult;
import com.nutricare.nutricare_api.core.application.port.in.ManageFoodItemUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateFoodItemRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateFoodItemRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.FoodItemWebMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FoodItemController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(FoodItemWebMapperImpl.class)
class FoodItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ManageFoodItemUseCase useCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void givenPageParams_whenListFoodItemIsRequested_thenReturnsMappedResultList() throws Exception {
        when(useCase.list(0, 10)).thenReturn(List.of(new FoodItemResult(1, "Rice", "Grain", Set.of())));

        mockMvc.perform(get("/api/foods").param("pageIndex", "0").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].name").value("Rice"));
    }

    @Test
    void givenExistingId_whenGetFoodItemIsRequested_thenReturnsMappedDetails() throws Exception {
        when(useCase.findById(1)).thenReturn(Optional.of(
                new FoodItemDetailsResult(1, "Rice", "Grain", 100.0, Set.of(), null, Set.of())));

        mockMvc.perform(get("/api/foods/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Rice"));
    }

    @Test
    void givenUnknownId_whenGetFoodItemIsRequested_thenPropagatesUnhandledNoSuchElementException() {
        when(useCase.findById(999)).thenReturn(Optional.empty());

        // No @ExceptionHandler maps NoSuchElementException (see GlobalExceptionHandler), so it propagates
        // uncaught here; a real deployed server would translate it to a 500 via the container's error page.
        assertThatThrownBy(() -> mockMvc.perform(get("/api/foods/999")))
                .hasCauseInstanceOf(NoSuchElementException.class);
    }

    @Test
    void givenValidRequest_whenCreateFoodItemIsPosted_thenReturns201WithLocationHeader() throws Exception {
        when(useCase.create(any())).thenReturn(
                new FoodItemDetailsResult(1, "Rice", "Grain", 100.0, Set.of(), null, Set.of()));

        mockMvc.perform(post("/api/foods")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new CreateFoodItemRequest("Rice", "Grain", 100.0, Set.of(), null, Set.of()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Rice"));
    }

    @Test
    void givenValidRequest_whenUpdateFoodItemIsPut_thenReturnsUpdatedDetails() throws Exception {
        when(useCase.update(any())).thenReturn(
                new FoodItemDetailsResult(1, "Brown Rice", "Grain", 120.0, Set.of(), null, Set.of()));

        mockMvc.perform(put("/api/foods/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(
                                new UpdateFoodItemRequest("Brown Rice", "Grain", 120.0, Set.of(), Set.of()))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Brown Rice"));
    }

    @Test
    void givenExistingId_whenDeleteFoodItemIsRequested_thenReturns204AndDelegatesToUseCase() throws Exception {
        mockMvc.perform(delete("/api/foods/1"))
                .andExpect(status().isNoContent());

        verify(useCase).deleteById(1);
    }
}
