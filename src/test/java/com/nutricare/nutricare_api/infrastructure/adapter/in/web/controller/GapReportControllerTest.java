package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;
import com.nutricare.nutricare_api.core.application.port.in.GetIntakeGapReportUseCase;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.GapReportWebMapperImpl;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GapReportController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GapReportWebMapperImpl.class)
class GapReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GetIntakeGapReportUseCase useCase;

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
    void givenAuthenticatedOwnerAndValidDate_whenGetReportIsRequested_thenReturnsMappedGapReport() throws Exception {
        LocalDate date = LocalDate.now().minusDays(1);
        when(useCase.getGapReport(1, date, 1, 10)).thenReturn(
                new GapReportResult("Vietnam RDA", date, LocalDateTime.now(), 400.0, 2000.0, Set.of(), Set.of()));

        mockMvc.perform(get("/api/profiles/1/intake-gap-report/" + date).param("standardId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.standard").value("Vietnam RDA"))
                .andExpect(jsonPath("$.data.consumedCalories").value(400.0));
    }
}
