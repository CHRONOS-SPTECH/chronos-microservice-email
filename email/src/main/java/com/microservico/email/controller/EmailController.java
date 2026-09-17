package com.microservico.email.controller;

import com.microservico.email.dtos.EmailDto;
import com.microservico.email.dtos.EmailResponseDto;
import com.microservico.email.mappers.EmailMapper;
import com.microservico.email.services.EmailService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/sending-email")
    public ResponseEntity<EmailResponseDto> sendingEmail(@RequestBody @Valid EmailDto emailDto) {
        var sentEmail = emailService.sendEmail(EmailMapper.toModel(emailDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(EmailResponseDto.from(sentEmail));
    }

}
