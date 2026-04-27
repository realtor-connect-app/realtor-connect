package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.config.EmailConfiguration;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import io.micrometer.core.annotation.Timed;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
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

    private final LinkedBlockingQueue<EmailMessage> emailQueue = new LinkedBlockingQueue<>();
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private Counter sentCounter;
    private Counter failedCounter;

    private final EmailSenderService emailSenderService;
    private final EmailConfiguration emailConfiguration;
    private final MeterRegistry meterRegistry;

    public void addToQueue(EmailMessage emailMessage) {
        if (shuttingDown.get()) {
            log.warn("Email processor is shutting down. Email {} to {} was not queued", emailMessage.getEmailTemplate(), emailMessage.getTo());
            throw new RuntimeException("Email processor is shutting down");
        }

        emailQueue.add(emailMessage);
        log.debug("Queued {} email to {}", emailMessage.getEmailTemplate(), emailMessage.getTo());
    }

    @PostConstruct
    public void init() {
        Gauge.builder("email.processor.queue.size", emailQueue::size).register(meterRegistry);
        sentCounter = Counter.builder("email.processor.count").tag("type", "SENT").register(meterRegistry);
        failedCounter = Counter.builder("email.processor.count").tag("type", "FAILED").register(meterRegistry);
    }

    @PreDestroy
    public void shutdown() {
        log.info("Graceful shutdown started for email processor. Remaining emails: {}", emailQueue.size());
        shuttingDown.set(true);
        processBatch(emailQueue.size());
        log.info("Email processor graceful shutdown completed. Queue is empty");
    }

    @Scheduled(fixedDelayString = "${email.processor.processing-delay-ms:1000}")
    protected void processEmails() {
        if (shuttingDown.get()) {
            return;
        }
        processBatch(emailConfiguration.getProcessor().getBatchSize());
    }

    @Timed(value = "email.processor.service", histogram = true)
    protected void processBatch(int batchSize) {
        List<EmailMessage> emails = new LinkedList<>();
        emailQueue.drainTo(emails, batchSize);

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

        sentCounter.increment(sentCount.get());
        failedCounter.increment(failedCount.get());

        log.debug("Batch processed: {} sent, {} failed", sentCount.get(), failedCount.get());
    }

    private void mapResult(boolean success, AtomicInteger sentCount, AtomicInteger failedCount) {
        if (success) {
            sentCount.incrementAndGet();
        } else {
            failedCount.incrementAndGet();
        }
    }

}
