package com.microservico.email.services.ports;

import com.microservico.email.models.EmailModel;

public interface EmailSender {

    void send(EmailModel email);
}
