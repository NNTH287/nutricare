package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nutricare.nutricare_api.core.application.dto.AuthenticatedUserResult;
import com.nutricare.nutricare_api.core.application.port.in.AuthenticateUserUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RefreshTokenUseCase;
import com.nutricare.nutricare_api.core.application.port.in.RegisterUserUseCase;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidCredentialsException;
import com.nutricare.nutricare_api.core.domain.entity.user.InvalidRefreshTokenException;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.LoginRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.RegisterRequest;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.AuthWebMapperImpl;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.JwtAuthenticationFilter;
import com.nutricare.nutricare_api.infrastructure.adapter.out.security.JwtProperties;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({AuthWebMapperImpl.class, AuthenticationControllerTest.TestJwtPropertiesConfig.class})
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private RegisterUserUseCase registerUseCase;

    @MockitoBean
    private AuthenticateUserUseCase authenticateUseCase;

    @MockitoBean
    private RefreshTokenUseCase refreshTokenUseCase;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @TestConfiguration
    static class TestJwtPropertiesConfig {
        @Bean
        JwtProperties jwtProperties() {
            return new JwtProperties("test-secret-not-for-production-use-only-in-tests", 15, 7);
        }
    }

    @Test
    void givenNewEmail_whenRegisterIsPosted_thenReturns201WithAuthResponseBodyAndHttpOnlyCookie() throws Exception {
        when(registerUseCase.register(any())).thenReturn(
                new AuthenticatedUserResult("access-jwt", "refresh-jwt", 1, "user@example.com", Role.USER));

        mockMvc.perform(post("/api/auth/register")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new RegisterRequest("user@example.com", "raw-password"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.token").value("access-jwt"))
                .andExpect(jsonPath("$.data.email").value("user@example.com"))
                .andExpect(cookie().value("refresh_token", "refresh-jwt"))
                .andExpect(cookie().httpOnly("refresh_token", true))
                .andExpect(cookie().path("refresh_token", "/api/auth/refresh"));
    }

    @Test
    void givenCorrectCredentials_whenLoginIsPosted_thenReturns200WithAuthResponseBodyAndHttpOnlyCookie() throws Exception {
        when(authenticateUseCase.login("user@example.com", "raw-password")).thenReturn(
                new AuthenticatedUserResult("access-jwt", "refresh-jwt", 1, "user@example.com", Role.USER));

        mockMvc.perform(post("/api/auth/login")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(new LoginRequest("user@example.com", "raw-password"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("access-jwt"))
                .andExpect(cookie().value("refresh_token", "refresh-jwt"))
                .andExpect(cookie().httpOnly("refresh_token", true));
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

    @Test
    void givenValidRefreshTokenCookie_whenRefreshIsPosted_thenReturns200WithNewAccessTokenAndRotatedCookie() throws Exception {
        when(refreshTokenUseCase.refresh("old-refresh-jwt")).thenReturn(
                new AuthenticatedUserResult("new-access-jwt", "new-refresh-jwt", 1, "user@example.com", Role.USER));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token","old-refresh-jwt")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token").value("new-access-jwt"))
                .andExpect(cookie().value("refresh_token", "new-refresh-jwt"));
    }

    @Test
    void givenInvalidRefreshTokenCookie_whenRefreshIsPosted_thenReturns401ViaGlobalExceptionHandler() throws Exception {
        when(refreshTokenUseCase.refresh("bad-refresh-jwt"))
                .thenThrow(new InvalidRefreshTokenException("Refresh token is invalid or expired"));

        mockMvc.perform(post("/api/auth/refresh")
                        .cookie(new Cookie("refresh_token","bad-refresh-jwt")))
                .andExpect(status().isUnauthorized());
    }
}
