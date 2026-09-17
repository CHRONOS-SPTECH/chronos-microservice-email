package com.microservico.email.application.service;

import com.microservico.email.application.exception.EmailDeliveryException;
import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.out.EmailRepository;
import com.microservico.email.domain.model.Email;
import com.microservico.email.domain.model.EmailStatus;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SendEmailServiceTest {

    private static final Clock CLOCK = Clock.fixed(Instant.parse("2026-09-16T12:00:00Z"), ZoneOffset.UTC);

    @Test
    void marksEmailAsSentWhenGatewayDeliversIt() {
        var persistedEmail = new AtomicReference<Email>();
        EmailRepository repository = email -> {
            persistedEmail.set(email);
            return Email.reconstitute(1L, email.getOwnerRef(), email.getFrom(), email.getTo(), email.getSubject(),
                    email.getBody(), email.getSentAt(), email.getStatus());
        };
        var service = new SendEmailService(email -> { }, repository, CLOCK);

        var result = service.execute(command());

        assertEquals(EmailStatus.SENT, result.getStatus());
        assertEquals(LocalDateTime.of(2026, 9, 16, 12, 0), result.getSentAt());
        assertEquals(EmailStatus.SENT, persistedEmail.get().getStatus());
    }

    @Test
    void recordsFailureWhenGatewayCannotDeliverEmail() {
        var service = new SendEmailService(
                email -> { throw new EmailDeliveryException(new RuntimeException("SMTP unavailable")); },
                email -> email,
                CLOCK
        );

        var result = service.execute(command());

        assertEquals(EmailStatus.ERROR, result.getStatus());
        assertEquals(LocalDateTime.of(2026, 9, 16, 12, 0), result.getSentAt());
    }

    private SendEmailCommand command() {
        return new SendEmailCommand("account-1", "from@example.com", "to@example.com", "Subject", "Body");
    }
}
