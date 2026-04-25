package com.makurohashami.realtorconnect.email.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Email {

    private Long id;
    private String to;
    private String subject;
    private String body;
    private boolean isHtml;
    private EmailStatus status;
    private Instant createdAt;
    private Instant updatedAt;

}
