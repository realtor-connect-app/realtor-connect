package com.makurohashami.realtorconnect.service.email;

import com.makurohashami.realtorconnect.config.KafkaTopics;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEmailProducer {

    private final KafkaTopics kafkaTopics;
    private final KafkaTemplate<String, EmailMessage> emailKafkaTemplate;

    public void send(EmailMessage message) {
        emailKafkaTemplate.send(kafkaTopics.getTopics().getEmails().getName(), message);
    }

}
