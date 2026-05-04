package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.model.EmailMessage;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessorService {

    private Counter sentCounter;
    private Counter failedCounter;

    private final EmailSenderService emailSenderService;
    private final MeterRegistry meterRegistry;

    @PostConstruct
    public void init() {
        sentCounter = Counter.builder("email.processor.count").tag("status", "SENT").register(meterRegistry);
        failedCounter = Counter.builder("email.processor.count").tag("status", "FAILED").register(meterRegistry);
    }

    @Timed(value = "email.processor.service", histogram = true)
    public void processEmail(EmailMessage emailMessage) {
        log.debug("Processing {} email to {}", emailMessage.getEmailTemplate(), emailMessage.getTo());

        boolean sent = emailSenderService.send(emailMessage);
        mapResult(sent);

        log.debug("Email {} to {} processed. Sent: {}", emailMessage.getEmailTemplate(), emailMessage.getTo(), sent);
    }

    private void mapResult(boolean sent) {
        if (sent) {
            sentCounter.increment();
        } else {
            failedCounter.increment();
        }
    }

}
