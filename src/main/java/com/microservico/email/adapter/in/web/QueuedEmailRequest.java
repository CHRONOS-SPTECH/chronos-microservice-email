package com.microservico.email.adapter.in.web;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record QueuedEmailRequest(
        @NotBlank @Size(max = 255) String ownerRef,
        @NotBlank @Email @Size(max = 254) String emailFrom,
        @NotBlank @Email @Size(max = 254) String emailTo,
        @NotBlank @Size(max = 255) String subject,
        @NotBlank @Size(max = 100_000) String text
) {
}
