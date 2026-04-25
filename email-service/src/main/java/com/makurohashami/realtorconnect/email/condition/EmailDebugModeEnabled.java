package com.makurohashami.realtorconnect.email.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class EmailDebugModeEnabled extends AllNestedConditions {

    public EmailDebugModeEnabled() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(name = "email.debug-mode.enabled", havingValue = "true")
    static class DebugEnabledCondition {
    }

}
