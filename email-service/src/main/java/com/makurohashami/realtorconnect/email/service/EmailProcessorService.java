package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.config.EmailProcessorProperties;
import com.makurohashami.realtorconnect.email.dao.EmailDAO;
import com.makurohashami.realtorconnect.email.model.Email;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import com.makurohashami.realtorconnect.email.model.EmailStatus;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailProcessorService {

    private final EmailDAO emailDAO;
    private final EmailSenderService emailSenderService;
    private final SpringTemplateEngine springTemplateEngine;
    private final EmailProcessorProperties emailProcessorProperties;

    public void addToQueue(EmailMessage emailMessage) {
        emailDAO.save(buildEmail(emailMessage));
        log.debug("Email to {} queued with status NEW", emailMessage.getTo());
    }

    @Scheduled(fixedDelayString = "${email.processor.processing-delay-ms:1000}")
    protected void processEmails() {
        List<Email> emails = emailDAO.fetchAndSetStatusBatch(emailProcessorProperties.getBatchSize());
        if (CollectionUtils.isEmpty(emails)) {
            return;
        }
        log.debug("Processing {} emails", emails.size());

        ConcurrentLinkedQueue<Long> sentIds = new ConcurrentLinkedQueue<>();
        ConcurrentLinkedQueue<Long> failedIds = new ConcurrentLinkedQueue<>();

        List<CompletableFuture<Void>> futures = emails.stream()
                .map(email -> emailSenderService.send(email)
                        .thenAccept(success -> {
                            if (success) {
                                sentIds.add(email.getId());
                            } else {
                                failedIds.add(email.getId());
                            }
                        }))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        emailDAO.batchUpdateStatus(new ArrayList<>(sentIds), EmailStatus.SENT);
        emailDAO.batchUpdateStatus(new ArrayList<>(failedIds), EmailStatus.FAILED);

        log.debug("Batch processed: {} sent, {} failed", sentIds.size(), failedIds.size());
    }

    private Email buildEmail(EmailMessage emailMessage) {
        String body = springTemplateEngine.process(
                emailMessage.getEmailTemplate().getTemplatePath(),
                new Context(emailMessage.getLocale(), emailMessage.getParams())
        );

        return Email.builder()
                .to(emailMessage.getTo())
                .subject(emailMessage.getEmailTemplate().getSubject())
                .body(body)
                .isHtml(emailMessage.getEmailTemplate().isHtml())
                .status(EmailStatus.NEW)
                .createdAt(Instant.now())
                .build();
    }

}
