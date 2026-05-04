package com.makurohashami.realtorconnect.email.config;

import com.makurohashami.realtorconnect.email.config.KafkaTopics.ErrorHandler;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.Serializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DelegatingByTypeSerializer;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
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
    public ProducerFactory<Object, Object> emailDeadLetterProducerFactory() {
        Map<String, Object> props = kafkaProperties.buildProducerProperties();

        return new DefaultKafkaProducerFactory<>(
                props,
                () -> new DelegatingByTypeSerializer(deadLetterKeySerializers(), true),
                () -> new DelegatingByTypeSerializer(deadLetterValueSerializers(), true)
        );
    }

    @Bean
    public KafkaTemplate<Object, Object> emailDeadLetterKafkaTemplate() {
        return new KafkaTemplate<>(emailDeadLetterProducerFactory());
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
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                emailDeadLetterKafkaTemplate(),
                (record, exception) -> new TopicPartition(errorHandler.getDeadLetterTopic(), 1)
        );

        return new DefaultErrorHandler(recoverer, new FixedBackOff(
                errorHandler.getMaxAttempts(),
                errorHandler.getDelayMs()
        ));
    }

    private Serializer<EmailMessage> emailMessageJsonSerializer() {
        return new JsonSerializer<EmailMessage>().noTypeInfo();
    }

    private Map<Class<?>, Serializer<?>> deadLetterKeySerializers() {
        Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
        serializers.put(String.class, new StringSerializer());
        serializers.put(byte[].class, new ByteArraySerializer());
        return serializers;
    }

    private Map<Class<?>, Serializer<?>> deadLetterValueSerializers() {
        Map<Class<?>, Serializer<?>> serializers = new HashMap<>();
        serializers.put(EmailMessage.class, emailMessageJsonSerializer());
        serializers.put(byte[].class, new ByteArraySerializer());
        return serializers;
    }

}
