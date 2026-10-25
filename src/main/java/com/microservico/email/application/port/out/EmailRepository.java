package com.microservico.email.application.port.out;

import com.microservico.email.domain.model.Email;

public interface EmailRepository {

    Email save(Email email);
}
