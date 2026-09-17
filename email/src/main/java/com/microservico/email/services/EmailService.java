package com.microservico.email.services;

import com.microservico.email.enums.StatusEmail;
import com.microservico.email.models.EmailModel;
import com.microservico.email.services.ports.EmailDeliveryException;
import com.microservico.email.services.ports.EmailPersistence;
import com.microservico.email.services.ports.EmailSender;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class EmailService {

    private final EmailPersistence emailPersistence;
    private final EmailSender emailSender;

    public EmailService(EmailPersistence emailPersistence, EmailSender emailSender) {
        this.emailPersistence = emailPersistence;
        this.emailSender = emailSender;
    }

    public EmailModel sendEmail(EmailModel emailModel) {
        emailModel.setSendDateEmail(LocalDateTime.now());
        try {
            emailSender.send(emailModel);
            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (EmailDeliveryException e) {
            emailModel.setStatusEmail(StatusEmail.ERROR);
        }

        return emailPersistence.save(emailModel);
    }

}
