package com.makurohashami.realtorconnect.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "kafka")
public class KafkaTopics {

    private Topics topics;

    @Getter
    @Setter
    public static class Topics {
        private Topic emails;
    }

    @Getter
    @Setter
    public static class Topic {
        private String name;
        private int concurrency;
        private ErrorHandler errorHandler;
    }

    @Getter
    @Setter
    public static class ErrorHandler {
        private int maxAttempts;
        private int delayMs;
        private String deadLetterTopic;
    }

}
