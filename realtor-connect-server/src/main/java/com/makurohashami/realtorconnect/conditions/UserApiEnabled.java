package com.makurohashami.realtorconnect.conditions;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class UserApiEnabled extends AllNestedConditions {

    public UserApiEnabled() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.enable.user-api", havingValue = "true")
    static class UserApiEnabledCondition {
    }

}
