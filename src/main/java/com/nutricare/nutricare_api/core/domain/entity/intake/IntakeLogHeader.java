package com.nutricare.nutricare_api.core.domain.entity.intake;

import java.time.LocalDate;

public record IntakeLogHeader(Integer id, Integer profileId ,LocalDate logDate) {
}
