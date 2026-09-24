package com.nutricare.nutricare_api.core.domain.entity.calculation.spec;

import com.nutricare.nutricare_api.core.domain.entity.profile.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileSpecificationTest {

    private static Profile profile(GroupType groupType, SexType sexType, int ageYears, Integer trimester) {
        LocalDateTime now = LocalDateTime.now();
        return Profile.reconstitute(1, 10, "Jane", groupType, sexType,
                LocalDate.now().minusYears(ageYears), 60.0, 165.0, ActivityLevel.MODERATE, trimester, Set.of(), now, now);
    }

    @Test
    void givenMatchingGroupType_whenIsSatisfiedByIsCalled_thenReturnsTrue() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new GroupTypeMatches(GroupType.ADULT).isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenDifferentGroupType_whenIsSatisfiedByIsCalled_thenReturnsFalse() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new GroupTypeMatches(GroupType.CHILD).isSatisfiedBy(profile)).isFalse();
    }

    @Test
    void givenAgeWithinRange_whenIsSatisfiedByIsCalled_thenReturnsTrue() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new AgeInMonthsWithin(0, 1200).isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenAgeOutsideRange_whenIsSatisfiedByIsCalled_thenReturnsFalse() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new AgeInMonthsWithin(0, 100).isSatisfiedBy(profile)).isFalse();
    }

    @Test
    void givenMatchingTrimester_whenIsSatisfiedByIsCalled_thenReturnsTrue() {
        Profile profile = profile(GroupType.PREGNANT, SexType.FEMALE, 30, 2);

        assertThat(new TrimesterMatches(2).isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenBothNullTrimesters_whenIsSatisfiedByIsCalled_thenReturnsTrue() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new TrimesterMatches(null).isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenMatchingSexType_whenIsSatisfiedByIsCalled_thenReturnsTrue() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new SexMatches(SexType.FEMALE).isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenDifferentSexType_whenIsSatisfiedByIsCalled_thenReturnsFalse() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        assertThat(new SexMatches(SexType.MALE).isSatisfiedBy(profile)).isFalse();
    }

    @Test
    void givenAllComponentsSatisfied_whenAndIsCombined_thenReturnsTrue() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        ProfileSpecification combined = new GroupTypeMatches(GroupType.ADULT)
                .and(new SexMatches(SexType.FEMALE))
                .and(new AgeInMonthsWithin(0, 1200))
                .and(new TrimesterMatches(null));

        assertThat(combined.isSatisfiedBy(profile)).isTrue();
    }

    @Test
    void givenOneComponentUnsatisfied_whenAndIsCombined_thenReturnsFalse() {
        Profile profile = profile(GroupType.ADULT, SexType.FEMALE, 30, null);

        ProfileSpecification combined = new GroupTypeMatches(GroupType.ADULT)
                .and(new SexMatches(SexType.MALE));

        assertThat(combined.isSatisfiedBy(profile)).isFalse();
    }
}
