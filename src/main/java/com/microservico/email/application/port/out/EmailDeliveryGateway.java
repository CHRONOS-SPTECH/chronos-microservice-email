package com.microservico.email.application.port.out;

import com.microservico.email.domain.model.Email;

public interface EmailDeliveryGateway {

    void send(Email email);
}
