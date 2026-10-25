package com.microservico.email.adapter.in.web;

import java.util.UUID;

public record QueuedEmailResponse(UUID messageId, String status) {
    public QueuedEmailResponse(UUID messageId) {
        this(messageId, "ACCEPTED");
    }
}
