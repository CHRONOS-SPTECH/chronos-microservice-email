package com.microservico.email.infrastructure.mail;

import com.microservico.email.models.EmailModel;
import com.microservico.email.services.ports.EmailDeliveryException;
import com.microservico.email.services.ports.EmailSender;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class SmtpEmailSender implements EmailSender {

    private final JavaMailSender mailSender;

    public SmtpEmailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(EmailModel email) {
        try {
            var message = new SimpleMailMessage();
            message.setFrom(email.getEmailFrom());
            message.setTo(email.getEmailTo());
            message.setSubject(email.getSubject());
            message.setText(email.getText());
            mailSender.send(message);
        } catch (MailException exception) {
            throw new EmailDeliveryException(exception);
        }
    }
}
