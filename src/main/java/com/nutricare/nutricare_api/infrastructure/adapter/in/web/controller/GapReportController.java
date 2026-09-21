package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.port.in.GetIntakeGapReportUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.GapReportResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.GapReportWebMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
public class GapReportController {
    private final GetIntakeGapReportUseCase useCase;
    private final GapReportWebMapper mapper;

    @GetMapping("/api/profiles/{profileId}/intake-gap-report/{date}")
    public ResponseEntity<ApiResponse<GapReportResponse>> getReport(@PathVariable Integer profileId,
                                                                     @PathVariable LocalDate date,
                                                                     @RequestParam Integer standardId) {
        return ApiResponse.ok(mapper.toResponse(useCase.getGapReport(profileId, date, standardId)));
    }
}
