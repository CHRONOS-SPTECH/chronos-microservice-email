package com.microservico.email.application.port.in;

public record SendEmailCommand(
        String ownerRef,
        String from,
        String to,
        String subject,
        String body
) {
}
