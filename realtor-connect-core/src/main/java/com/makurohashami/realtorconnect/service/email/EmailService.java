package com.makurohashami.realtorconnect.service.email;

import com.makurohashami.realtorconnect.email.model.EmailMessage;
import com.makurohashami.realtorconnect.email.model.EmailTemplate;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import com.makurohashami.realtorconnect.entity.user.User;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${network.verifyEmailUrl}")
    private String verifyEmailUrl;

    private static final DateTimeFormatter EMAIL_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneOffset.UTC);

    private final KafkaEmailProducer kafkaEmailProducer;

    private void sendEmail(EmailMessage message) {
        kafkaEmailProducer.send(message);
    }

    @Async("emailExecutor")
    public void sendVerifyEmail(User user, String token) {
        sendEmail(EmailMessage.builder()
                .to(user.getEmail())
                .emailTemplate(EmailTemplate.VERIFY_EMAIL)
                .params(Map.of(
                        "name", user.getName(),
                        "link", verifyEmailUrl + token
                ))
                .build()
        );
    }

    @Async("emailExecutor")
    public void sendStartPremium(Realtor realtor, int durationInMonths) {
        sendEmail(EmailMessage.builder()
                .to(realtor.getEmail())
                .emailTemplate(EmailTemplate.PREMIUM_ADDED)
                .params(Map.of(
                        "name", realtor.getName(),
                        "durationInMonths", durationInMonths,
                        "expiresAt", EMAIL_DATE_FORMATTER.format(realtor.getPremiumExpiresAt())
                ))
                .build()
        );
    }

    @Async("emailExecutor")
    public void sendPremiumExpires(Realtor realtor) {
        long daysLeft = ChronoUnit.DAYS.between(Instant.now(), realtor.getPremiumExpiresAt()) + 1;
        sendEmail(EmailMessage.builder()
                .to(realtor.getEmail())
                .emailTemplate(EmailTemplate.PREMIUM_EXPIRES)
                .params(Map.of(
                        "name", realtor.getName(),
                        "daysLeft", daysLeft,
                        "expiresAt", EMAIL_DATE_FORMATTER.format(realtor.getPremiumExpiresAt())
                ))
                .build()
        );
    }

    @Async("emailExecutor")
    public void sendPremiumExpired(Realtor realtor) {
        sendEmail(EmailMessage.builder()
                .to(realtor.getEmail())
                .emailTemplate(EmailTemplate.PREMIUM_EXPIRED)
                .params(Map.of(
                        "name", realtor.getName()
                ))
                .build()
        );
    }

    @Async("emailExecutor")
    public void sendPasswordReset(User user, String token) {
        sendEmail(EmailMessage.builder()
                .to(user.getEmail())
                .emailTemplate(EmailTemplate.PASSWORD_RESET)
                .params(Map.of(
                        "name", user.getName(),
                        "token", token
                ))
                .build()
        );
    }

}
