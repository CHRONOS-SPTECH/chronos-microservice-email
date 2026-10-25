package com.microservico.email.adapter.out.persistence;

import com.microservico.email.domain.model.EmailStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "TB_EMAIL")
public class EmailEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "email_id")
    private Long id;
    private String ownerRef;
    private String emailFrom;
    private String emailTo;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String text;
    private LocalDateTime sendDateEmail;
    @Enumerated(EnumType.STRING)
    private EmailStatus statusEmail;

    protected EmailEntity() {
    }

    public Long getId() { return id; }
    public String getOwnerRef() { return ownerRef; }
    public String getEmailFrom() { return emailFrom; }
    public String getEmailTo() { return emailTo; }
    public String getSubject() { return subject; }
    public String getText() { return text; }
    public LocalDateTime getSendDateEmail() { return sendDateEmail; }
    public EmailStatus getStatusEmail() { return statusEmail; }

    public void setId(Long id) { this.id = id; }
    public void setOwnerRef(String ownerRef) { this.ownerRef = ownerRef; }
    public void setEmailFrom(String emailFrom) { this.emailFrom = emailFrom; }
    public void setEmailTo(String emailTo) { this.emailTo = emailTo; }
    public void setSubject(String subject) { this.subject = subject; }
    public void setText(String text) { this.text = text; }
    public void setSendDateEmail(LocalDateTime sendDateEmail) { this.sendDateEmail = sendDateEmail; }
    public void setStatusEmail(EmailStatus statusEmail) { this.statusEmail = statusEmail; }
}
