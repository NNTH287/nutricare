package com.nutricare.nutricare_api.core.domain.entity;

public final class TrimesterPolicy {
    private TrimesterPolicy() {}

    static void validate(GroupType groupType, Integer trimester) {
        if(groupType == GroupType.PREGNANT) {
            if(trimester == null || trimester < 1 || trimester > 3) {
                throw new InvalidTrimesterException("A PREGNANT group requires a trimester between 1 and 3.");
            }
        } else if (trimester != null) {
            throw new InvalidTrimesterException("Only a 'PREGNANT' group may have a trimester!");
        }
    }
}
