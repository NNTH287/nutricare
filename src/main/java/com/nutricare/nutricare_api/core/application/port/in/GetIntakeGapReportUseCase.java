package com.nutricare.nutricare_api.core.application.port.in;

import com.nutricare.nutricare_api.core.application.dto.GapReportResult;

import java.time.LocalDate;

public interface GetIntakeGapReportUseCase {
    GapReportResult getGapReport(Integer profileId, LocalDate date, Integer nutritionStandardId);
}
