package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import com.nutricare.nutricare_api.core.application.dto.CalculateNutrientResult;
import com.nutricare.nutricare_api.core.application.port.in.CalculateNutrientUseCase;
import com.nutricare.nutricare_api.core.domain.entity.user.Role;
import com.nutricare.nutricare_api.infrastructure.adapter.in.web.security.AuthenticatedUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * {@link CalculationController#calculate} and {@link CalculationController#calculateAndSave}
 * combine several independent reads (profile, standard, requirements, energy coefficients) that
 * must reflect one snapshot. Plain {@code readOnly = true} at the default isolation gives no such
 * guarantee — Postgres takes a fresh snapshot per statement under READ_COMMITTED, not per
 * transaction. Only an explicit {@code REPEATABLE_READ} does. This test checks the isolation
 * level actually in effect, not just that a transaction exists.
 */
@SpringBootTest
class ControllerTransactionBoundaryTest {

    @Autowired
    private CalculationController controller;

    @MockitoBean
    private CalculateNutrientUseCase useCase;

    private static final AuthenticatedUserPrincipal PRINCIPAL = new AuthenticatedUserPrincipal(1, "probe@example.com", Role.USER);

    @Test
    void givenCalculateIsCalled_thenTransactionIsReadOnlyAndRepeatableRead() {
        AtomicBoolean active = new AtomicBoolean();
        AtomicBoolean readOnly = new AtomicBoolean();
        AtomicReference<Integer> isolationLevel = new AtomicReference<>();

        when(useCase.calculateNutrientResult(any(), any(), any())).thenAnswer(invocation -> {
            active.set(TransactionSynchronizationManager.isActualTransactionActive());
            readOnly.set(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            isolationLevel.set(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());
            return sampleResult();
        });

        controller.calculate(1, 1, PRINCIPAL);

        assertThat(active).isTrue();
        assertThat(readOnly).isTrue();
        assertThat(isolationLevel).hasValue(TransactionDefinition.ISOLATION_REPEATABLE_READ);
    }

    @Test
    void givenCalculateAndSaveIsCalled_thenTransactionIsWritableAndRepeatableRead() {
        AtomicBoolean active = new AtomicBoolean();
        AtomicBoolean readOnly = new AtomicBoolean(true);
        AtomicReference<Integer> isolationLevel = new AtomicReference<>();

        when(useCase.saveCalculationResult(any(), any(), any())).thenAnswer(invocation -> {
            active.set(TransactionSynchronizationManager.isActualTransactionActive());
            readOnly.set(TransactionSynchronizationManager.isCurrentTransactionReadOnly());
            isolationLevel.set(TransactionSynchronizationManager.getCurrentTransactionIsolationLevel());
            return sampleResult();
        });

        controller.calculateAndSave(1, 1, PRINCIPAL);

        assertThat(active).isTrue();
        assertThat(readOnly).isFalse();
        assertThat(isolationLevel).hasValue(TransactionDefinition.ISOLATION_REPEATABLE_READ);
    }

    private static CalculateNutrientResult sampleResult() {
        return new CalculateNutrientResult("standard", LocalDateTime.now(), 2000.0, Set.of(), Set.of());
    }
}
