package com.makurohashami.realtorconnect.config;

import io.micrometer.core.aop.CountedAspect;
import io.micrometer.core.aop.TimedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Bean
    public TimedAspect timedAspect() {
        return new TimedAspect();
    }

    @Bean
    public CountedAspect countedAspect() {
        return new CountedAspect();
    }

}
