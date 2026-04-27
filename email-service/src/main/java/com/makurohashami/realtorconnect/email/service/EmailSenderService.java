package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.model.EmailMessage;
import io.micrometer.core.annotation.Timed;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final String ENCODING = "UTF-8";

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine springTemplateEngine;

    @Async("emailExecutor")
    @Timed(value = "email.sender.service", histogram = true)
    public CompletableFuture<Boolean> send(EmailMessage emailMessage) {
        boolean success = true;
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true, ENCODING);

            messageHelper.setTo(emailMessage.getTo());
            messageHelper.setSubject(emailMessage.getEmailTemplate().getSubject());
            messageHelper.setText(buildBody(emailMessage), emailMessage.getEmailTemplate().isHtml());

            mailSender.send(messageHelper.getMimeMessage());
        } catch (Exception ex) {
            log.error("Error while sending email {} to: {}", emailMessage.getEmailTemplate(), emailMessage.getTo(), ex);
            success = false;
        }
        return CompletableFuture.completedFuture(success);
    }

    private String buildBody(EmailMessage emailMessage) {
        return springTemplateEngine.process(
                emailMessage.getEmailTemplate().getTemplatePath(),
                new Context(emailMessage.getLocale(), emailMessage.getParams())
        );
    }

}
