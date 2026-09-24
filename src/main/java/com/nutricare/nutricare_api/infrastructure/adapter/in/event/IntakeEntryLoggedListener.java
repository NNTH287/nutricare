package com.nutricare.nutricare_api.infrastructure.adapter.in.event;

import com.nutricare.nutricare_api.core.application.port.out.ApplicationMetrics;
import com.nutricare.nutricare_api.core.domain.event.IntakeEntryLogged;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IntakeEntryLoggedListener {
    private final ApplicationMetrics metrics;

    @EventListener
    public void onIntakeEntryLogged(IntakeEntryLogged event) {
        metrics.recordIntakeEntryLogged();
    }
}
