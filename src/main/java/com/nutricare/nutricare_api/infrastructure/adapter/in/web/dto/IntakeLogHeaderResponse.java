package com.nutricare.nutricare_api.infrastructure.adapter.in.web.dto;

import java.time.LocalDate;

public record IntakeLogHeaderResponse(Integer id, Integer profileId, LocalDate logDate) {
}
