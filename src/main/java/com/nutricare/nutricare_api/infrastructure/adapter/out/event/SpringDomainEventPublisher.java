package com.nutricare.nutricare_api.infrastructure.adapter.out.event;

import com.nutricare.nutricare_api.core.application.port.out.DomainEventPublisher;
import com.nutricare.nutricare_api.core.domain.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }
}
