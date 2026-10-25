package com.microservico.email.adapter.in.web;

import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.in.SendEmailUseCase;
import com.microservico.email.application.port.out.EmailQueuePublisher;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final SendEmailUseCase sendEmailUseCase;
    private final ObjectProvider<EmailQueuePublisher> emailQueuePublisher;

    public EmailController(SendEmailUseCase sendEmailUseCase, ObjectProvider<EmailQueuePublisher> emailQueuePublisher) {
        this.sendEmailUseCase = sendEmailUseCase;
        this.emailQueuePublisher = emailQueuePublisher;
    }

    @PostMapping("/sending-email")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody @Valid EmailRequest request) {
        var command = new SendEmailCommand(
                request.ownerRef(), request.emailFrom(), request.emailTo(), request.subject(), request.text()
        );
        var email = sendEmailUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmailResponse.from(email));
    }

    @PostMapping("/sending-email/async")
    public ResponseEntity<QueuedEmailResponse> queueEmail(@RequestBody @Valid QueuedEmailRequest request) {
        var publisher = emailQueuePublisher.getIfAvailable();
        if (publisher == null) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        var command = new SendEmailCommand(
                request.ownerRef(), request.emailFrom(), request.emailTo(), request.subject(), request.text()
        );
        try {
            var messageId = publisher.publish(command);
            return ResponseEntity.status(HttpStatus.ACCEPTED).body(new QueuedEmailResponse(messageId));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
    }
}
