package com.makurohashami.realtorconnect.email.listener;

import com.makurohashami.realtorconnect.email.model.EmailMessage;
import com.makurohashami.realtorconnect.email.service.EmailProcessorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailKafkaListener {

    private final EmailProcessorService emailProcessorService;

    @KafkaListener(
            topics = "emails",
            containerFactory = "emailKafkaListenerContainerFactory"
    )
    public void onEmailMessage(EmailMessage message) {
        emailProcessorService.addToQueue(message);
    }

}
