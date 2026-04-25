package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.model.Email;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSenderService {

    private static final String ENCODING = "UTF-8";

    private final JavaMailSender mailSender;

    @Async("emailExecutor")
    public CompletableFuture<Boolean> send(Email email) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true, ENCODING);

            messageHelper.setTo(email.getTo());
            messageHelper.setSubject(email.getSubject());
            messageHelper.setText(email.getBody(), email.isHtml());

            mailSender.send(messageHelper.getMimeMessage());
            return CompletableFuture.completedFuture(true);
        } catch (Exception ex) {
            log.error("Error while sending email to: {}", email.getTo(), ex);
        }
        return CompletableFuture.completedFuture(false);
    }

}
