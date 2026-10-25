package com.microservico.email.application.service;

import com.microservico.email.application.exception.EmailDeliveryException;
import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.in.SendEmailUseCase;
import com.microservico.email.application.port.out.EmailDeliveryGateway;
import com.microservico.email.application.port.out.EmailRepository;
import com.microservico.email.domain.model.Email;

import java.time.Clock;
import java.time.LocalDateTime;

public class SendEmailService implements SendEmailUseCase {

    private final EmailDeliveryGateway deliveryGateway;
    private final EmailRepository emailRepository;
    private final Clock clock;

    public SendEmailService(EmailDeliveryGateway deliveryGateway, EmailRepository emailRepository, Clock clock) {
        this.deliveryGateway = deliveryGateway;
        this.emailRepository = emailRepository;
        this.clock = clock;
    }

    @Override
    public Email execute(SendEmailCommand command) {
        var email = Email.create(
                command.ownerRef(), command.from(), command.to(), command.subject(), command.body()
        );
        var processedAt = LocalDateTime.now(clock);

        try {
            deliveryGateway.send(email);
            email.markAsSent(processedAt);
        } catch (EmailDeliveryException exception) {
            email.markAsError(processedAt);
        }

        return emailRepository.save(email);
    }
}
