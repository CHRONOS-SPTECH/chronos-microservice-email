package com.microservico.email.adapter.out.mail;

import com.microservico.email.application.exception.EmailDeliveryException;
import com.microservico.email.application.port.out.EmailDeliveryGateway;
import com.microservico.email.domain.model.Email;
import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

public class SmtpEmailDeliveryGateway implements EmailDeliveryGateway {

    private static final Logger logger = LoggerFactory.getLogger(SmtpEmailDeliveryGateway.class);

    private final JavaMailSender mailSender;

    public SmtpEmailDeliveryGateway(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(Email email) {
        try {
            var mimeMessage = mailSender.createMimeMessage();
            var message = new MimeMessageHelper(mimeMessage, "UTF-8");
            message.setFrom(email.getFrom());
            message.setTo(email.getTo());
            message.setSubject(email.getSubject());
            message.setText(email.getBody(), true);
            mailSender.send(mimeMessage);
            logger.info("E-mail enviado com sucesso para {}", email.getTo());
        } catch (MailException | MessagingException exception) {
            throw new EmailDeliveryException(exception);
        }
    }
}
