package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.port.out.TokenIssuer;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenIssuer tokenIssuer;

    @Test
    void givenNoCredentials_whenHealthIsRequested_thenReturns200() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk());
    }

    @Test
    void givenNoCredentials_whenMetricsIsRequested_thenReturns403() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenUserRoleToken_whenMetricsIsRequested_thenReturns403() throws Exception {
        String token = tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER);

        mockMvc.perform(get("/actuator/metrics").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void givenAdminRoleToken_whenMetricsIsRequested_thenReturns200() throws Exception {
        String token = tokenIssuer.issueAccessToken(1, "admin@example.com", Role.ADMIN);

        mockMvc.perform(get("/actuator/metrics").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void givenAdminRoleToken_whenLoggersIsRequested_thenReturns200() throws Exception {
        String token = tokenIssuer.issueAccessToken(1, "admin@example.com", Role.ADMIN);

        mockMvc.perform(get("/actuator/loggers").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void givenUserRoleToken_whenLoggersIsRequested_thenReturns403() throws Exception {
        String token = tokenIssuer.issueAccessToken(1, "user@example.com", Role.USER);

        mockMvc.perform(get("/actuator/loggers").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
