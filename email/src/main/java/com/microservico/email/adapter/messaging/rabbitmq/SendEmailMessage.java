package com.microservico.email.adapter.messaging.rabbitmq;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

public record SendEmailMessage(
        @NotNull UUID messageId,
        @NotBlank @Size(max = 255) String ownerRef,
        @NotBlank @Email @Size(max = 254) String from,
        @NotBlank @Email @Size(max = 254) String to,
        @NotBlank @Size(max = 255) String subject,
        @NotBlank @Size(max = 100_000) String body
) {
}
