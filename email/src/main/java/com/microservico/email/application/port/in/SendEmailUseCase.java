package com.microservico.email.application.port.in;

import com.microservico.email.domain.model.Email;

public interface SendEmailUseCase {

    Email execute(SendEmailCommand command);
}
