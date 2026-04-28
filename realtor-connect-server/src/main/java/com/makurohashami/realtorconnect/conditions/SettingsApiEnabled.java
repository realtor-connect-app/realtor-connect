package com.makurohashami.realtorconnect.conditions;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class SettingsApiEnabled extends AllNestedConditions {

    public SettingsApiEnabled() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.enable.settings-api", havingValue = "true")
    static class UserApiEnabledCondition {
    }

}
