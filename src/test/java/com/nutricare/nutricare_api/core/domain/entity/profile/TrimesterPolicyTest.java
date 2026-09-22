package com.nutricare.nutricare_api.core.domain.entity.profile;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TrimesterPolicyTest {

    @ParameterizedTest
    @EnumSource(value = GroupType.class, names = "PREGNANT", mode = EnumSource.Mode.EXCLUDE)
    void givenNonPregnantGroupAndNullTrimester_whenValidated_thenNoExceptionIsThrown(GroupType groupType) {
        assertThatCode(() -> TrimesterPolicy.validate(groupType, null))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @EnumSource(value = GroupType.class, names = "PREGNANT", mode = EnumSource.Mode.EXCLUDE)
    void givenNonPregnantGroupAndNonNullTrimester_whenValidated_thenThrowsInvalidTrimesterException(GroupType groupType) {
        assertThatThrownBy(() -> TrimesterPolicy.validate(groupType, 1))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @Test
    void givenPregnantGroupAndNullTrimester_whenValidated_thenThrowsInvalidTrimesterException() {
        assertThatThrownBy(() -> TrimesterPolicy.validate(GroupType.PREGNANT, null))
                .isInstanceOf(InvalidTrimesterException.class);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    void givenPregnantGroupAndTrimesterWithinRange_whenValidated_thenNoExceptionIsThrown(int trimester) {
        assertThatCode(() -> TrimesterPolicy.validate(GroupType.PREGNANT, trimester))
                .doesNotThrowAnyException();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 4})
    void givenPregnantGroupAndTrimesterOutsideRange_whenValidated_thenThrowsInvalidTrimesterException(int trimester) {
        assertThatThrownBy(() -> TrimesterPolicy.validate(GroupType.PREGNANT, trimester))
                .isInstanceOf(InvalidTrimesterException.class);
    }
}
