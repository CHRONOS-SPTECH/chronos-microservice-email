package com.microservico.email.domain.model;

import java.time.LocalDateTime;

public final class Email {

    private final Long id;
    private final String ownerRef;
    private final String from;
    private final String to;
    private final String subject;
    private final String body;
    private LocalDateTime sentAt;
    private EmailStatus status;

    private Email(Long id, String ownerRef, String from, String to, String subject, String body,
                  LocalDateTime sentAt, EmailStatus status) {
        this.id = id;
        this.ownerRef = ownerRef;
        this.from = from;
        this.to = to;
        this.subject = subject;
        this.body = body;
        this.sentAt = sentAt;
        this.status = status;
    }

    public static Email create(String ownerRef, String from, String to, String subject, String body) {
        return new Email(null, ownerRef, from, to, subject, body, null, null);
    }

    public static Email reconstitute(Long id, String ownerRef, String from, String to, String subject, String body,
                                     LocalDateTime sentAt, EmailStatus status) {
        return new Email(id, ownerRef, from, to, subject, body, sentAt, status);
    }

    public void markAsSent(LocalDateTime sentAt) {
        this.sentAt = sentAt;
        this.status = EmailStatus.SENT;
    }

    public void markAsError(LocalDateTime sentAt) {
        this.sentAt = sentAt;
        this.status = EmailStatus.ERROR;
    }

    public Long getId() { return id; }
    public String getOwnerRef() { return ownerRef; }
    public String getFrom() { return from; }
    public String getTo() { return to; }
    public String getSubject() { return subject; }
    public String getBody() { return body; }
    public LocalDateTime getSentAt() { return sentAt; }
    public EmailStatus getStatus() { return status; }
}
