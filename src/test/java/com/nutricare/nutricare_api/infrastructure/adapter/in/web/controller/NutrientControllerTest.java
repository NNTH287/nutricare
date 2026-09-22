package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricare.nutricare_api.core.application.dto.NutrientResult;
import com.nutricare.nutricare_api.core.application.port.in.ManageNutrientUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateNutrientRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateNutrientRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.NutrientWebMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NutrientController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(NutrientWebMapperImpl.class)
class NutrientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ManageNutrientUseCase useCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void givenExistingId_whenGetNutrientIsRequested_thenReturnsMappedNutrient() throws Exception {
        when(useCase.findById(1)).thenReturn(Optional.of(new NutrientResult(1, "protein", "Protein", "g")));

        mockMvc.perform(get("/api/nutrients/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.code").value("protein"));
    }

    @Test
    void givenValidRequest_whenCreateNutrientIsPosted_thenReturns201WithMappedNutrient() throws Exception {
        when(useCase.create(any())).thenReturn(new NutrientResult(1, "protein", "Protein", "g"));

        mockMvc.perform(post("/api/nutrients")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CreateNutrientRequest("protein", "Protein", "g"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.code").value("protein"));
    }

    @Test
    void givenValidRequest_whenUpdateNutrientIsPut_thenReturnsUpdatedNutrient() throws Exception {
        when(useCase.update(any())).thenReturn(new NutrientResult(1, "protein", "Protein (updated)", "g"));

        mockMvc.perform(put("/api/nutrients/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateNutrientRequest("protein", "Protein (updated)", "g"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value("Protein (updated)"));
    }

    @Test
    void givenExistingId_whenDeleteNutrientIsRequested_thenReturns204AndDelegatesToUseCase() throws Exception {
        mockMvc.perform(delete("/api/nutrients/1"))
                .andExpect(status().isNoContent());

        verify(useCase).deleteById(1);
    }
}
