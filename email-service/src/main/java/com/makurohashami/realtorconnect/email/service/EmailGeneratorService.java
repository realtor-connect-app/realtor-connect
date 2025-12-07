package com.makurohashami.realtorconnect.email.service;

import com.makurohashami.realtorconnect.email.model.Email;
import com.makurohashami.realtorconnect.email.model.EmailTemplate;
import com.makurohashami.realtorconnect.entity.realtor.Realtor;
import com.makurohashami.realtorconnect.entity.user.User;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailGeneratorService {

    @Value("${network.verifyEmailUrl}")
    private String verifyEmailUrl;

    private final SpringTemplateEngine templateEngine;

    public Email generateVerifyEmail(User user, String token) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getName());
        templateVariables.put("link", verifyEmailUrl + token);

        return generateEmail(user.getEmail(), EmailTemplate.VERIFY_EMAIL, templateVariables);
    }

    public Email generateStartPremiumEmail(Realtor realtor, int durationInMonths) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", realtor.getName());
        templateVariables.put("durationInMonths", durationInMonths);
        templateVariables.put("expiresAt", realtor.getPremiumExpiresAt());

        return generateEmail(realtor.getEmail(), EmailTemplate.PREMIUM_ADDED, templateVariables);
    }

    public Email generatePremiumExpiresEmail(Realtor realtor) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", realtor.getName());
        templateVariables.put("daysLeft", ChronoUnit.DAYS.between(Instant.now(), realtor.getPremiumExpiresAt()) + 1);
        templateVariables.put("expiresAt", realtor.getPremiumExpiresAt());

        return generateEmail(realtor.getEmail(), EmailTemplate.PREMIUM_EXPIRES, templateVariables);
    }

    public Email generatePremiumExpiredEmail(Realtor realtor) {
        return generateEmail(realtor.getEmail(), EmailTemplate.PREMIUM_EXPIRED, Map.of("name", realtor.getName()));
    }

    public Email generateResetPasswordEmail(User user, String token) {
        Map<String, Object> templateVariables = new HashMap<>();
        templateVariables.put("name", user.getName());
        templateVariables.put("token", token);

        return generateEmail(user.getEmail(), EmailTemplate.RESET_PASSWORD, templateVariables);
    }

    private Email generateEmail(String to, EmailTemplate template, Map<String, Object> templateVariables) {
        Context context = new Context();
        context.setVariables(templateVariables);
        String body = templateEngine.process(template.getTemplatePath(), context);

        return Email.builder()
                .to(to)
                .body(body)
                .isHtml(template.isHtml())
                .subject(template.getSubject())
                .build();
    }
}
