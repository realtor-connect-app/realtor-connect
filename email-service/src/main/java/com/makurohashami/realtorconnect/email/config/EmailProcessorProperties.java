package com.makurohashami.realtorconnect.email.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "email.processor")
public class EmailProcessorProperties {

    private int processingDelayMs;
    private int batchSize;

}
