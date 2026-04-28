package com.makurohashami.realtorconnect.conditions;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class AdminApiEnabled extends AllNestedConditions {

    public AdminApiEnabled() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.enable.admin-api", havingValue = "true")
    static class AdminApiEnabledCondition {
    }

}
