package com.microservico.email.application.port.out;

import com.microservico.email.application.port.in.SendEmailCommand;

import java.util.UUID;

public interface EmailQueuePublisher {
    UUID publish(SendEmailCommand command);
}
