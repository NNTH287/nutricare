package com.nutricare.nutricare_api.core.application.dto;

import java.time.LocalDate;

public record IntakeLogHeaderResult(Integer id, Integer profileId, LocalDate logDate) {
}
