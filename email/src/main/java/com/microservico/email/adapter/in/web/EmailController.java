package com.microservico.email.adapter.in.web;

import com.microservico.email.application.port.in.SendEmailCommand;
import com.microservico.email.application.port.in.SendEmailUseCase;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EmailController {

    private final SendEmailUseCase sendEmailUseCase;

    public EmailController(SendEmailUseCase sendEmailUseCase) {
        this.sendEmailUseCase = sendEmailUseCase;
    }

    @PostMapping("/sending-email")
    public ResponseEntity<EmailResponse> sendEmail(@RequestBody @Valid EmailRequest request) {
        var command = new SendEmailCommand(
                request.ownerRef(), request.emailFrom(), request.emailTo(), request.subject(), request.text()
        );
        var email = sendEmailUseCase.execute(command);
        return ResponseEntity.status(HttpStatus.CREATED).body(EmailResponse.from(email));
    }
}
