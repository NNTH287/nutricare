package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Only {@link CalculationController} and {@link GapReportController} have a genuine need for a
 * transaction boundary: each combines several independent reads that must reflect one snapshot,
 * which only an elevated isolation level actually provides. Every other controller method makes
 * at most one persistence write, which Spring Data already wraps in its own transaction, so
 * wrapping the controller method again adds nothing. This test locks in that decision on both
 * sides — it fails if the boundary quietly disappears from where it matters, and it fails if
 * {@code @Transactional} quietly reappears somewhere it doesn't.
 */
@SpringBootTest
class ControllerTransactionWiringTest {

    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private CalculationController calculationController;

    @Autowired
    private FoodItemController foodItemController;

    @Autowired
    private GapReportController gapReportController;

    @Autowired
    private IntakeLoggingController intakeLoggingController;

    @Autowired
    private NutrientController nutrientController;

    @Test
    void calculationAndGapReportControllersCarryTransactionAdvice() {
        assertTransactional(calculationController);
        assertTransactional(gapReportController);
    }

    @Test
    void everyOtherControllerCarriesNoTransactionAdvice() {
        assertNotTransactional(authenticationController);
        assertNotTransactional(foodItemController);
        assertNotTransactional(intakeLoggingController);
        assertNotTransactional(nutrientController);
    }

    private static void assertTransactional(Object bean) {
        assertThat(AopUtils.isAopProxy(bean))
                .as("%s should be an AOP proxy", bean.getClass().getSimpleName())
                .isTrue();
        assertThat(((Advised) bean).getAdvisors())
                .as("%s should carry transaction advice", bean.getClass().getSimpleName())
                .anySatisfy(advisor -> assertThat(advisor.getAdvice()).isInstanceOf(TransactionInterceptor.class));
    }

    private static void assertNotTransactional(Object bean) {
        boolean hasTransactionAdvice = AopUtils.isAopProxy(bean)
                && ((Advised) bean).getAdvisors().length > 0
                && java.util.Arrays.stream(((Advised) bean).getAdvisors())
                        .anyMatch(advisor -> advisor.getAdvice() instanceof TransactionInterceptor);

        assertThat(hasTransactionAdvice)
                .as("%s should not carry transaction advice", bean.getClass().getSimpleName())
                .isFalse();
    }
}
