package com.makurohashami.realtorconnect.email.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmailTemplate {

    VERIFY_EMAIL(1, "Welcome to Realtor Connect!", "email/verify_email.html", true),
    PREMIUM_ADDED(2, "Congratulations on your new subscription", "email/premium_added.html", true),
    PREMIUM_EXPIRES(3, "Your subscription will expire soon", "email/premium_expires.html", true),
    PREMIUM_EXPIRED(4, "Your subscription has expired", "email/premium_expired.html", true),
    PASSWORD_RESET(5, "Reset password", "email/reset_password.html", true);

    @JsonValue
    private final int id;
    private final String subject;
    private final String templatePath;
    private final boolean isHtml;

    private static final Map<Integer, EmailTemplate> TEMPLATE_BY_ID_MAP = Collections.unmodifiableMap(initialiseValueMapping());

    @JsonCreator
    public static EmailTemplate fromId(int id) {
        return Optional.ofNullable(TEMPLATE_BY_ID_MAP.get(id))
                .orElseThrow(() -> new IllegalArgumentException("Can't find EmailTemplate with id: " + id));
    }

    private static Map<Integer, EmailTemplate> initialiseValueMapping() {
        return Stream.of(values()).collect(HashMap::new,
                (map, template) -> map.put(template.id, template), HashMap::putAll
        );
    }

}
