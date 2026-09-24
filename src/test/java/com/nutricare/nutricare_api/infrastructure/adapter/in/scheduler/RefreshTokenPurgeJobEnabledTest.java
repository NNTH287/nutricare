package com.nutricare.nutricare_api.infrastructure.adapter.in.scheduler;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestPropertySource(properties = "app.scheduling.enabled=true")
class RefreshTokenPurgeJobEnabledTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void givenSchedulingExplicitlyEnabled_thenRefreshTokenPurgeJobBeanIsPresent() {
        assertThat(context.getBeanNamesForType(RefreshTokenPurgeJob.class)).isNotEmpty();
    }
}
