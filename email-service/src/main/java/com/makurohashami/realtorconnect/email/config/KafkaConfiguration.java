package com.makurohashami.realtorconnect.email.config;

import com.makurohashami.realtorconnect.email.config.KafkaTopics.ErrorHandler;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@RequiredArgsConstructor
public class KafkaConfiguration {

    private final KafkaProperties kafkaProperties;
    private final KafkaTopics kafkaTopics;

    @Bean
    public ConsumerFactory<String, EmailMessage> emailConsumerFactory() {
        Map<String, Object> props = kafkaProperties.buildConsumerProperties();

        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        props.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);

        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, EmailMessage.class.getName());
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "com.makurohashami.realtorconnect.email");
        props.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailMessage> emailKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, EmailMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(emailConsumerFactory());
        factory.setCommonErrorHandler(emailKafkaErrorHandler());

        return factory;
    }

    @Bean
    public DefaultErrorHandler emailKafkaErrorHandler() {
        ErrorHandler errorHandler = kafkaTopics.getTopics().getEmails().getErrorHandler();
        return new DefaultErrorHandler(new FixedBackOff(
                errorHandler.getMaxAttempts(),
                errorHandler.getDelayMs()
        ));
    }

}
