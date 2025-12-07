package com.makurohashami.realtorconnect.cdn.condition;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class CloudinaryCdnEnabled extends AllNestedConditions {

    public CloudinaryCdnEnabled() {
        super(ConfigurationPhase.REGISTER_BEAN);
    }

    @ConditionalOnProperty(name = "cdn.client", havingValue = "cloudinary")
    static class CloudinaryIsCdnClient {
    }

}
