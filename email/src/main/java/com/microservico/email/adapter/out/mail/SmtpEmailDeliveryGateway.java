package com.microservico.email.adapter.out.mail;

import com.microservico.email.application.exception.EmailDeliveryException;
import com.microservico.email.application.port.out.EmailDeliveryGateway;
import com.microservico.email.domain.model.Email;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class SmtpEmailDeliveryGateway implements EmailDeliveryGateway {

    private final JavaMailSender mailSender;

    public SmtpEmailDeliveryGateway(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Email email) {
        try {
            var message = new SimpleMailMessage();
            message.setFrom(email.getFrom());
            message.setTo(email.getTo());
            message.setSubject(email.getSubject());
            message.setText(email.getBody());
            mailSender.send(message);
        } catch (MailException exception) {
            throw new EmailDeliveryException(exception);
        }
    }
}
