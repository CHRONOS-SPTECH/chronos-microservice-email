package com.microservico.email.services.ports;

import com.microservico.email.models.EmailModel;

public interface EmailPersistence {

    EmailModel save(EmailModel email);
}
