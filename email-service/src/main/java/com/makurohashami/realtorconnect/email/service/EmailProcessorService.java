package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.config.EmailProcessorProperties;
import com.makurohashami.realtorconnect.email.dao.EmailDAO;
import com.makurohashami.realtorconnect.email.model.Email;
import com.makurohashami.realtorconnect.email.model.EmailMessage;
import com.makurohashami.realtorconnect.email.model.EmailStatus;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
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
        List<Email> emails = emailDAO.findByStatus(EmailStatus.NEW, emailProcessorProperties.getBatchSize());

        if (emails.isEmpty()) {
            return;
        }

        log.debug("Processing {} emails", emails.size());

        List<CompletableFuture<Void>> futures = emails.stream()
                .map(email -> emailSenderService.send(email)
                        .thenAccept(success -> handleSendResult(email, success)))
                .toList();

        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
    }

    private void handleSendResult(Email email, boolean success) {
        emailDAO.updateStatus(email.getId(), success ? EmailStatus.SENT : EmailStatus.FAILED);
        log.debug("Email {} to {} sent. Success: {}", email.getId(), email.getTo(), success);
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
