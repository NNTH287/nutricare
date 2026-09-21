package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.port.in.CalculateNutrientUseCase;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.api.ApiResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto.CalculateNutrientResponse;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.mapper.CalculationWebMapper;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.AuthenticatedUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CalculationController {
    private final CalculateNutrientUseCase useCase;
    private final CalculationWebMapper mapper;

    @GetMapping("/api/profile/{profileId}/nutrient-calculation")
    public ResponseEntity<ApiResponse<CalculateNutrientResponse>> calculate(@PathVariable Integer profileId,
                                                                              @RequestParam Integer standardId,
                                                                              @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ApiResponse.ok(mapper.toResponse(useCase.calculateNutrientResult(profileId, standardId, principal.userId())));
    }

    @PostMapping("/api/profile/{profileId}/nutrient-calculation")
    public ResponseEntity<ApiResponse<CalculateNutrientResponse>> calculateAndSave(@PathVariable Integer profileId,
                                                                                     @RequestParam Integer standardId,
                                                                                     @AuthenticationPrincipal AuthenticatedUserPrincipal principal) {
        return ApiResponse.ok(mapper.toResponse(useCase.saveCalculationResult(profileId, standardId, principal.userId())));
    }
}
