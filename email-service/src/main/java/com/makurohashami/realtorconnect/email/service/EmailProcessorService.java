package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.config.EmailConfiguration;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessorService {

    private static final LinkedBlockingQueue<EmailMessage> emailQueue = new LinkedBlockingQueue<>();

    private final EmailSenderService emailSenderService;
    private final EmailConfiguration emailConfiguration;

    public void addToQueue(EmailMessage emailMessage) {
        emailQueue.add(emailMessage);
        log.debug("Queued {} email to {}", emailMessage.getEmailTemplate(), emailMessage.getTo());
    }

    @Scheduled(fixedDelayString = "${email.processor.processing-delay-ms:1000}")
    protected void processEmails() {
        List<EmailMessage> emails = new LinkedList<>();
        emailQueue.drainTo(emails, emailConfiguration.getProcessor().getBatchSize());
        if (CollectionUtils.isEmpty(emails)) {
            return;
        }
        log.debug("Processing {} emails", emails.size());

        AtomicInteger sentCount = new AtomicInteger();
        AtomicInteger failedCount = new AtomicInteger();

        List<CompletableFuture<Void>> futures = emails.stream()
                .map(email -> emailSenderService.send(email)
                        .thenAccept(success -> mapResult(success, sentCount, failedCount)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        log.debug("Batch processed: {} sent, {} failed", sentCount.get(), failedCount.get());
    }

    private void mapResult(boolean success, AtomicInteger sentCount, AtomicInteger failedCount) {
        if (success) {
            sentCount.getAndIncrement();
        } else {
            failedCount.getAndIncrement();
        }
    }

}
