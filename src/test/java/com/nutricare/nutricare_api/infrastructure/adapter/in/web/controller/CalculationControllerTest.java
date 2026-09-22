package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.core.application.port.in.CalculateNutrientUseCase;
import com.nutricare.nutricare_api.core.domain.entity.profile.ProfileAccessDeniedException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.CalculationWebMapperImpl;
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

import java.time.LocalDateTime;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CalculationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CalculationWebMapperImpl.class)
class CalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CalculateNutrientUseCase useCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @BeforeEach
    void authenticateAsUser() {
        AuthenticatedUserPrincipal principal = new AuthenticatedUserPrincipal(10, "user@example.com", Role.USER);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, java.util.List.of()));
    }

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void givenAuthenticatedOwner_whenCalculateIsRequested_thenReturnsMappedResult() throws Exception {
        when(useCase.calculateNutrientResult(1, 1, 10)).thenReturn(
                new CalculateNutrientResult("Vietnam RDA", LocalDateTime.now(), 2000.0, Set.of(), Set.of()));

        mockMvc.perform(get("/api/profile/1/nutrient-calculation").param("standardId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.standard").value("Vietnam RDA"));
    }

    @Test
    void givenAuthenticatedOwner_whenCalculateAndSaveIsPosted_thenReturnsMappedResult() throws Exception {
        when(useCase.saveCalculationResult(1, 1, 10)).thenReturn(
                new CalculateNutrientResult("Vietnam RDA", LocalDateTime.now(), 2000.0, Set.of(), Set.of()));

        mockMvc.perform(post("/api/profile/1/nutrient-calculation").param("standardId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.standard").value("Vietnam RDA"));
    }

    @Test
    void givenNonOwnerPrincipal_whenCalculateIsRequested_thenReturns403ViaGlobalExceptionHandler() throws Exception {
        when(useCase.calculateNutrientResult(1, 1, 10))
                .thenThrow(new ProfileAccessDeniedException("Profile 1 is not owned by user 10"));

        mockMvc.perform(get("/api/profile/1/nutrient-calculation").param("standardId", "1"))
                .andExpect(status().isForbidden());
    }
}
