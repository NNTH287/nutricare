package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.LoginRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.AuthWebMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.JwtAuthenticationFilter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(AuthWebMapperImpl.class)
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RegisterUserUseCase registerUseCase;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void givenNewEmail_whenRegisterIsPosted_thenReturns201WithAuthResponseBody() throws Exception {
        when(registerUseCase.register(any())).thenReturn(
                new AuthenticatedUserResult("jwt-token", 1, "user@example.com", Role.USER));

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new RegisterRequest("user@example.com", "raw-password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").value("jwt-token"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"));
    }

    @Test
    void givenCorrectCredentials_whenLoginIsPosted_thenReturns200WithAuthResponseBody() throws Exception {
        when(authenticateUseCase.login("user@example.com", "raw-password")).thenReturn(
                new AuthenticatedUserResult("jwt-token", 1, "user@example.com", Role.USER));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@example.com", "raw-password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("jwt-token"));
    }

    @Test
    void givenInvalidCredentials_whenLoginIsPosted_thenReturns401ViaGlobalExceptionHandler() throws Exception {
        when(authenticateUseCase.login("user@example.com", "wrong-password"))
                .thenThrow(new InvalidCredentialsException("Invalid email or password"));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@example.com", "wrong-password"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid email or password"));
    }
}
