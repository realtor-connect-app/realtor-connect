package com.makurohashami.realtorconnect.email.model;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {

    private String to;
    private EmailTemplate emailTemplate;
    @Builder.Default
    private Map<String, Object> params = new HashMap<>();
    @Builder.Default
    private Locale locale = Locale.getDefault();

}
