package com.microservico.email.adapter.in.web;

import com.microservico.email.domain.model.Email;
import com.microservico.email.domain.model.EmailStatus;

import java.time.LocalDateTime;

public record EmailResponse(
        Long emailId,
        String ownerRef,
        String emailFrom,
        String emailTo,
        String subject,
        String text,
        LocalDateTime sendDateEmail,
        EmailStatus statusEmail
) {
    public static EmailResponse from(Email email) {
        return new EmailResponse(
                email.getId(), email.getOwnerRef(), email.getFrom(), email.getTo(), email.getSubject(),
                email.getBody(), email.getSentAt(), email.getStatus()
        );
    }
}
