package com.microservico.email.dtos;

import com.microservico.email.enums.StatusEmail;
import com.microservico.email.models.EmailModel;

import java.time.LocalDateTime;

public record EmailResponseDto(
        Long emailId,
        String ownerRef,
        String emailFrom,
        String emailTo,
        String subject,
        String text,
        LocalDateTime sendDateEmail,
        StatusEmail statusEmail
) {
    public static EmailResponseDto from(EmailModel email) {
        return new EmailResponseDto(
                email.getEmailId(),
                email.getOwnerRef(),
                email.getEmailFrom(),
                email.getEmailTo(),
                email.getSubject(),
                email.getText(),
                email.getSendDateEmail(),
                email.getStatusEmail()
        );
    }
}
