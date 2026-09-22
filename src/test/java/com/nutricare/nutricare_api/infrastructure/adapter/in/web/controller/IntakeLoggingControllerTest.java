package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricare.nutricare_api.core.application.dto.IntakeEntryResult;
import com.nutricare.nutricare_api.core.application.dto.IntakeLogHeaderResult;
import com.nutricare.nutricare_api.core.application.port.in.LogIntakeUseCase;
import com.nutricare.nutricare_api.core.domain.entity.menu.MealSlot;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CreateIntakeEntryRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.UpdateIntakeEntryRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.IntakeLogWebMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.AuthenticatedUserPrincipal;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(IntakeLoggingController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(IntakeLogWebMapperImpl.class)
class IntakeLoggingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @MockitoBean
    private LogIntakeUseCase useCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void authenticateAsUser() {
        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(10, "user@example.com", Role.USER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenAuthenticatedOwner_whenListByProfileIsRequested_thenReturnsMappedHeaderList() throws Exception {
        when(useCase.listLogs(1, 0, 10, 10)).thenReturn(List.of(new IntakeLogHeaderResult(1, 1, LocalDate.now())));

        mockMvc.perform(get("/api/intake-logs/profiles/1").param("pageIndex", "0").param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").value(1));
    }

    @Test
    void givenAuthenticatedOwner_whenCreateLogIsPosted_thenReturns201WithMappedHeader() throws Exception {
        LocalDate date = LocalDate.now().minusDays(1);
        when(useCase.createLog(1, date, 10)).thenReturn(new IntakeLogHeaderResult(1, 1, date));

        mockMvc.perform(post("/api/intake-logs/profiles/1").param("date", date.toString()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.id").value(1));
    }

    @Test
    void givenAuthenticatedOwner_whenDeleteLogIsRequested_thenReturns204AndDelegatesToUseCase() throws Exception {
        mockMvc.perform(delete("/api/intake-logs/1"))
                .andExpect(status().isNoContent());

        verify(useCase).deleteLogById(1, 10);
    }

    @Test
    void givenAuthenticatedOwner_whenListEntriesIsRequested_thenReturnsMappedEntryList() throws Exception {
        LocalDate date = LocalDate.now().minusDays(1);
        when(useCase.listEntriesOnDate(1, date, 10)).thenReturn(
                List.of(new IntakeEntryResult(1, 5, 100.0, MealSlot.BREAKFAST)));

        mockMvc.perform(get("/api/intake-logs/profiles/1/" + date))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].foodItemId").value(5));
    }

    @Test
    void givenExistingEntryId_whenGetEntryIsRequested_thenReturnsMappedEntry() throws Exception {
        when(useCase.findEntryById(1)).thenReturn(
                java.util.Optional.of(new IntakeEntryResult(1, 5, 100.0, MealSlot.BREAKFAST)));

        mockMvc.perform(get("/api/intake-logs/entries/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.foodItemId").value(5));
    }

    @Test
    void givenAuthenticatedOwner_whenCreateEntryIsPosted_thenReturns201WithMappedEntry() throws Exception {
        when(useCase.createEntry(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(10))).thenReturn(
                new IntakeEntryResult(1, 5, 100.0, MealSlot.BREAKFAST));

        mockMvc.perform(post("/api/intake-logs/1/entries")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new CreateIntakeEntryRequest(5, 100.0, MealSlot.BREAKFAST))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.foodItemId").value(5));
    }

    @Test
    void givenAuthenticatedOwner_whenUpdateEntryIsPut_thenReturnsUpdatedEntry() throws Exception {
        when(useCase.updateEntry(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.eq(10))).thenReturn(
                new IntakeEntryResult(1, 5, 150.0, MealSlot.DINNER));

        mockMvc.perform(put("/api/intake-logs/1/entries/1")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new UpdateIntakeEntryRequest(150.0, MealSlot.DINNER))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.quantityG").value(150.0));
    }

    @Test
    void givenAuthenticatedOwner_whenDeleteEntryIsRequested_thenReturns204AndDelegatesToUseCase() throws Exception {
        mockMvc.perform(delete("/api/intake-logs/1/entries/2"))
                .andExpect(status().isNoContent());

        verify(useCase).deleteEntryById(1, 2, 10);
    }
}
