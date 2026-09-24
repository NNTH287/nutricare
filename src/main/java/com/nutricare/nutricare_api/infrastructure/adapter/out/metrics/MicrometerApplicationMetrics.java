package com.nutricare.nutricare_api.infrastructure.adapter.out.metrics;

import com.nutricare.nutricare_api.core.application.port.out.ApplicationMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class MicrometerApplicationMetrics implements ApplicationMetrics {
    private final MeterRegistry registry;

    @Override
    public void recordGapReportDuration(Duration duration) {
        registry.timer("nutricare.gapreport.duration").record(duration);
    }

    @Override
    public void recordLoginFailure(String reason) {
        registry.counter("nutricare.auth.login.failure", "reason", reason).increment();
    }

    @Override
    public void recordIntakeEntryLogged() {
        registry.counter("nutricare.intake.entries.logged").increment();
    }
}
