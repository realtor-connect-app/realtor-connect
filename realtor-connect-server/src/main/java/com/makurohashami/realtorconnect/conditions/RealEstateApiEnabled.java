package com.makurohashami.realtorconnect.conditions;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class RealEstateApiEnabled extends AllNestedConditions {

    public RealEstateApiEnabled() {
        super(ConfigurationPhase.PARSE_CONFIGURATION);
    }

    @ConditionalOnProperty(value = "feature.enable.real-estate-api", havingValue = "true")
    static class RealEstateApiEnabledCondition {
    }

}
