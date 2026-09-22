package com.nutricare.nutricare_api.core.domain.entity.calculation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NutrientGapTest {

    @Test
    void givenConsumedAmountBelowTarget_whenEvaluateIsCalled_thenStatusIsShortfall() {
        NutrientTarget target = new NutrientTarget(1, 50.0, 100.0);

        NutrientGap gap = NutrientGap.evaluate(1, 30.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.SHORTFALL);
        assertThat(gap.getShortfallOrOverAmountG()).isEqualTo(20.0);
    }

    @Test
    void givenConsumedAmountAboveMax_whenEvaluateIsCalled_thenStatusIsOverIntake() {
        NutrientTarget target = new NutrientTarget(1, 50.0, 100.0);

        NutrientGap gap = NutrientGap.evaluate(1, 150.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.OVER_INTAKE);
        assertThat(gap.getShortfallOrOverAmountG()).isEqualTo(50.0);
    }

    @Test
    void givenConsumedAmountBetweenTargetAndMax_whenEvaluateIsCalled_thenStatusIsOnTargetWithNoAmount() {
        NutrientTarget target = new NutrientTarget(1, 50.0, 100.0);

        NutrientGap gap = NutrientGap.evaluate(1, 75.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.ON_TARGET);
        assertThat(gap.getShortfallOrOverAmountG()).isNull();
    }

    @Test
    void givenConsumedAmountExactlyAtTarget_whenEvaluateIsCalled_thenStatusIsOnTarget() {
        NutrientTarget target = new NutrientTarget(1, 50.0, 100.0);

        NutrientGap gap = NutrientGap.evaluate(1, 50.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.ON_TARGET);
    }

    @Test
    void givenConsumedAmountExactlyAtMax_whenEvaluateIsCalled_thenStatusIsOnTarget() {
        NutrientTarget target = new NutrientTarget(1, 50.0, 100.0);

        NutrientGap gap = NutrientGap.evaluate(1, 100.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.ON_TARGET);
    }

    @Test
    void givenTargetWithNoMaxAmount_whenConsumedAmountExceedsTarget_thenStatusIsOnTargetNotOverIntake() {
        NutrientTarget target = new NutrientTarget(1, 50.0, null);

        NutrientGap gap = NutrientGap.evaluate(1, 1000.0, target);

        assertThat(gap.getStatus()).isEqualTo(NutrientGapStatus.ON_TARGET);
    }
}
