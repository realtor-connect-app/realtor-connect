package com.makurohashami.realtorconnect.config;

import com.makurohashami.realtorconnect.entity.realtor.enumeration.SubscriptionType;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "realtor")
public class RealtorConfiguration {

    private PlanConfiguration planConfiguration;
    private List<Integer> daysToNotifyExpiresPremium;

    public PlanProperties getPlanPropertiesByPlan(SubscriptionType type) {
        return switch (type) {
            case FREE -> planConfiguration.free;
            case PREMIUM -> planConfiguration.premium;
        };
    }

    @Getter
    @Setter
    public static class PlanConfiguration {
        private PlanProperties free;
        private PlanProperties premium;
    }

    @Getter
    @Setter
    public static class PlanProperties {
        private int maxPublicRealEstates;
    }

}
