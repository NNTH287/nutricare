package com.nutricare.nutricare_api.infrastructure.adapter.in.web.controller;

import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import static org.assertj.core.api.Assertions.assertThat;

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
    void multiWriteAndMultiReadControllersCarryTransactionAdvice() {
        assertTransactional(authenticationController);
        assertTransactional(calculationController);
        assertTransactional(gapReportController);
    }

    @Test
    void everyOtherControllerCarriesNoTransactionAdvice() {
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
