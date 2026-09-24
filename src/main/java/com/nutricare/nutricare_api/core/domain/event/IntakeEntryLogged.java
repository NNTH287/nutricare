package com.nutricare.nutricare_api.core.domain.event;

import java.time.Instant;
import java.time.LocalDate;

public record IntakeEntryLogged(Integer profileId, Integer foodItemId, Double quantityG, LocalDate date,
                                 Instant occurredAt) implements DomainEvent {
}
