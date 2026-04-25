package com.makurohashami.realtorconnect.email.config;

import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfiguration {

    /*@Value("${spring.kafka.bootstrap-servers:localhost:9092}")
    private String bootstrapServers;

    @Value("${spring.kafka.consumer.group-id:email-service}")
    private String groupId;

    @Bean
    public ConsumerFactory<String, EmailMessage> emailConsumerFactory(ObjectMapper objectMapper) {
        JsonDeserializer<EmailMessage> valueDeserializer = new JsonDeserializer<>(EmailMessage.class, objectMapper);
        valueDeserializer.setRemoveTypeHeaders(false);
        valueDeserializer.addTrustedPackages("*");

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(), valueDeserializer);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, EmailMessage> emailKafkaListenerContainerFactory(
            ConsumerFactory<String, EmailMessage> emailConsumerFactory) {
        ConcurrentKafkaListenerContainerFactory<String, EmailMessage> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(emailConsumerFactory);
        return factory;
    }*/

}
