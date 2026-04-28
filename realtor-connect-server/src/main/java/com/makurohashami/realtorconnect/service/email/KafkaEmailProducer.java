package com.makurohashami.realtorconnect.service.email;

import com.makurohashami.realtorconnect.config.KafkaTopics;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import io.micrometer.core.annotation.Counted;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaEmailProducer {

    private final KafkaTopics kafkaTopics;
    private final KafkaTemplate<String, EmailMessage> emailKafkaTemplate;

    @Counted(value = "realtorconnect.kafka.email.produced.count")
    @Timed(value = "realtorconnect.kafka.email.produce", histogram = true)
    public void send(EmailMessage message) {
        emailKafkaTemplate.send(kafkaTopics.getTopics().getEmails().getName(), message);
    }

}
