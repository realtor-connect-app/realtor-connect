package com.makurohashami.realtorconnect.conditions;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class SchedulerEnabled extends AllNestedConditions {

    public SchedulerEnabled() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.enable.scheduler", havingValue = "true")
    static class SchedulerEnabledCondition {
    }

}
