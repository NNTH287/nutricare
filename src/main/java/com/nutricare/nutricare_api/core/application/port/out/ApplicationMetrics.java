package com.nutricare.nutricare_api.core.application.port.out;

import java.time.Duration;

public interface ApplicationMetrics {
    void recordGapReportDuration(Duration duration);

    void recordLoginFailure(String reason);

    void recordIntakeEntryLogged();
}
