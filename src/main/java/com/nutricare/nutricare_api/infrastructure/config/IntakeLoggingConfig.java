package com.nutricare.nutricare_api.infrastructure.config;

import com.nutricare.nutricare_api.core.application.mapper.IntakeLoggingMapper;
import com.nutricare.nutricare_api.core.application.port.in.LogIntakeUseCase;
import com.nutricare.nutricare_api.core.application.port.out.DomainEventPublisher;
import com.nutricare.nutricare_api.core.application.port.out.IntakeLogRepository;
import com.nutricare.nutricare_api.core.application.port.out.ProfileRepository;
import com.nutricare.nutricare_api.core.application.service.LogIntakeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IntakeLoggingConfig {

    @Bean
    public IntakeLoggingMapper intakeLoggingMapper() {
        return new IntakeLoggingMapper();
    }

    @Bean
    public LogIntakeUseCase logIntakeUseCase(IntakeLogRepository intakeLogRepository, ProfileRepository profileRepository,
                                              DomainEventPublisher domainEventPublisher, IntakeLoggingMapper intakeLoggingMapper) {
        return new LogIntakeService(intakeLogRepository, profileRepository, domainEventPublisher, intakeLoggingMapper);
    }
}