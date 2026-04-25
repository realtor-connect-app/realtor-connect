package com.makurohashami.realtorconnect.email.service;


import com.makurohashami.realtorconnect.email.model.EmailMessage;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
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

    private final SpringTemplateEngine springTemplateEngine;
    private final JavaMailSender mailSender;

    @Async("emailExecutor")
    public void send(EmailMessage emailMessage) {
        try {
            MimeMessageHelper messageHelper = new MimeMessageHelper(mailSender.createMimeMessage(), true, ENCODING);

            messageHelper.setTo(emailMessage.getTo());
            messageHelper.setSubject(emailMessage.getEmailTemplate().getSubject());
            messageHelper.setText(buildBody(emailMessage), emailMessage.getEmailTemplate().isHtml());

            mailSender.send(messageHelper.getMimeMessage());
        } catch (MailException ex) {
            log.error("Error while sending email", ex);
        } catch (MessagingException ex) {
            log.error("MimeMessageHelper creation failed", ex);
        } catch (Exception ex) {
            log.error("Unknown error", ex);
        }
    }

    private String buildBody(EmailMessage emailMessage) {
        return springTemplateEngine.process(
                emailMessage.getEmailTemplate().getTemplatePath(),
                new Context(emailMessage.getLocale(), emailMessage.getParams())
        );
    }

}
